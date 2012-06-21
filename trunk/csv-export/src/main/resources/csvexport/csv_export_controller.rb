require 'fastercsv'

class Api::CsvExportWebServiceController < Api::ApiController

  # GET /api/csv_export?resource=<resource>&qualifiers=<qualifiers>&metrics=<metrics>&includerules=<true|false>
  def index
    resource=Project.by_key(params[:resource])
    not_found('Resource not found') unless resource

    snapshot=resource.last_snapshot
    not_found('Snapshot not found') unless snapshot
    access_denied unless has_role?(:user, snapshot)

    @includerules=(params[:includerules]=='true')
    if params[:qualifiers].present?
      # load descendants
      snapshot_conditions=['snapshots.islast=:islast']
      snapshot_values={:islast => true}

      snapshot_conditions << '(snapshots.id=:sid OR (snapshots.root_snapshot_id=:root_sid AND snapshots.path LIKE :path))'
      snapshot_values[:sid]=snapshot.id
      snapshot_values[:root_sid] = (snapshot.root_snapshot_id || snapshot.id)
      snapshot_values[:path]="#{snapshot.path}#{snapshot.id}.%"

      if params[:qualifiers].present?
        snapshot_conditions << 'snapshots.qualifier in (:qualifiers)'
        snapshot_values[:qualifiers]=params['qualifiers'].split(',')
      end

      # optimization: add condition on projects
      conditions=snapshot_conditions.join(' AND ') + ' AND projects.qualifier IN (:qualifiers)'
      @snapshots=Snapshot.find(:all, :conditions => [conditions, snapshot_values], :include => :project, :order => 'projects.long_name')
    else
      snapshot_conditions=['snapshots.id=:sid']
      snapshot_values={:sid => snapshot.id}
      @snapshots=[snapshot]
    end

    @metrics=[]
    @measures_hash_by_sid={}
    @violations_hash_by_sid={}

    if params['metrics']
      params['metrics'].each do |metric_key|
        @metrics<<Metric.by_key(metric_key)
      end
    end
    if !@metrics.empty? || @includerules
      @violation_metric=Metric.by_key('violations')
      measure_conditions=['project_measures.metric_id IN (:metrics) AND project_measures.characteristic_id IS NULL']
      measure_values={}
      if @includerules
        @metrics<<@violation_metric unless @metrics.include?(@violation_metric)
        measure_conditions<<'(project_measures.rule_id IS NULL OR project_measures.metric_id=:violationid)'
        measure_values[:violationid]=@violation_metric.id
      else
        measure_conditions<<'project_measures.rule_id IS NULL AND project_measures.rule_priority IS NULL'
      end
      measure_values[:metrics]=@metrics.select { |m| m.id }

      measures=ProjectMeasure.find(:all,
                                   :joins => :snapshot,
                                   :select => select_columns_for_measures(),
                                   :conditions => [(snapshot_conditions + measure_conditions).join(' AND '), snapshot_values.merge(measure_values)])

      measures.each do |measure|
        if measure.rule_id
          @violations_hash_by_sid[measure.snapshot_id]||={}
          @violations_hash_by_sid[measure.snapshot_id][measure.rule_id]=measure.value
        elsif measure.rule_priority.nil?
          @measures_hash_by_sid[measure.snapshot_id]||={}
          @measures_hash_by_sid[measure.snapshot_id][measure.metric_id]=measure
        end
      end
    end

    @rules=[]
    if @includerules
      profile_measure=snapshot.root_snapshot.measure('profile')
      profile=Profile.find(:first, :include => {:active_rules => :rule}, :conditions => ['id=?', profile_measure.value.to_i]) if profile_measure && profile_measure.value

      if profile
        @rules=profile.active_rules.map { |ar| ar.rule }.sort_by { |r| r.name }
      end
    end

    respond_to do |format|
      format.csv {
        send_data(to_csv, :type => 'text/csv; charset=utf-8; header=present', :disposition => 'attachment; filename=sonar.csv')
      }
    end
  end


  private

  def select_columns_for_measures
    'project_measures.id,project_measures.value,project_measures.text_value,project_measures.metric_id,project_measures.snapshot_id,project_measures.rule_id,project_measures.rule_priority'
  end

  def to_csv
    FasterCSV.generate do |csv|
      header = ['Name']
      @metrics.each do |metric|
        header<<metric.description
      end
      @rules.each do |rule|
        header<<rule.name
      end
      csv << header
      @snapshots.each do |snapshot|
        measures_hash=@measures_hash_by_sid[snapshot.id]
        row=[snapshot.project.long_name]
        @metrics.each do |metric|
          if measures_hash
            measure=measures_hash[metric.id]
            row<<(measure ? measure.typed_value : nil)
          else
            row<<nil
          end
        end

        violations_by_rule_id=@violations_hash_by_sid[snapshot.id]
        @rules.each do |rule|
          if violations_by_rule_id
            row<<(violations_by_rule_id[rule.id]||0)
          else
            row<<0
          end
        end
        csv<<row
      end
    end
  end
end
