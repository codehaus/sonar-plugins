class ComparingLoader

   attr_reader :last_snapshot_ids_by_language
   attr_reader :total_projects
   attr_reader :total_ncloc

   def initialize()
     last_snapshot_ids_by_language = Project.find(:all,
        :select => 'language, snapshots.id AS root_id',
        :joins => :snapshots,
        :conditions => ['snapshots.islast = ? AND snapshots.root_snapshot_id IS NULL AND snapshots.qualifier = ?', true, 'TRK'],
        :order => 'language ASC')

     @last_snapshot_ids_by_language = {}

     for last_snapshot_ids in last_snapshot_ids_by_language
       if @last_snapshot_ids_by_language[last_snapshot_ids.language] == nil 
         @last_snapshot_ids_by_language[last_snapshot_ids.language] = [last_snapshot_ids.root_id]
       else
         @last_snapshot_ids_by_language[last_snapshot_ids.language] = 
            @last_snapshot_ids_by_language[last_snapshot_ids.language] << last_snapshot_ids.root_id
       end
     end

     @total_ncloc = 0
     @total_projects = 0
   end

   def get_nb_projects_by_language
     result = {}

     @last_snapshot_ids_by_language.each_key { |key|
       hash_size = @last_snapshot_ids_by_language[key].length
       @total_projects += hash_size
       result[key] = hash_size
     }

     result
   end

   def get_sum_measure_by_language(metric_name)
     result = {}

     metric_measure_value = ''
     @last_snapshot_ids_by_language.each { |key, value|
       metric_measure_value = ProjectMeasure.sum(:value,
        :conditions => ['snapshot_id IN (:snapshot_id) AND metric_id = :metric_id', 
            {:snapshot_id => value, :metric_id => Metric.by_key(metric_name).id}])
       @total_ncloc += metric_measure_value.to_i
       result[key] = metric_measure_value.round
     }

     result
   end

   def get_avg_measure_for_language(metric_name, language)
     ProjectMeasure.average(:value,
        :conditions => ['snapshot_id IN (:snapshot_id) AND metric_id = :metric_id', 
            {:snapshot_id => @last_snapshot_ids_by_language[language], :metric_id => Metric.by_key(metric_name).id}])
   end

   def get_projects_for_language(language)
     Project.find(:all, 
        :select => 'id, name',
        :conditions => ["root_id IS NULL AND scope = 'PRJ' AND qualifier = 'TRK' AND language = ?", language],
        :order => 'name ASC')
   end

   def get_measure_value_for_other_project(metric_name, projectId)
     snapshot_project = Snapshot.find(:first, 
        :select => 'snapshots.id',
        :include => 'project', 
        :conditions => ['projects.id = ? AND islast = ? AND root_snapshot_id IS NULL AND snapshots.qualifier = ?', projectId, true, 'TRK'])
     
     project_measure = ProjectMeasure.find(:first, 
        :select => 'value',
        :conditions => ['rule_id is null AND person_id is null AND characteristic_id is null AND snapshot_id = ? AND metric_id = ?', snapshot_project.id, Metric.by_key(metric_name).id])

     result = nil
     if project_measure != nil
       result = project_measure.value
     end

     result
   end

   def get_project_name_by_id(project_id)
     Project.find(:first, :select => 'name', :conditions => ["id = ?", project_id]).name
   end

   def get_nb_projects_for_language(language)
      @last_snapshot_ids_by_language[language].length
   end

   def get_last_snapshot_ids_by_language
     @last_snapshot_ids_by_language
   end

   def get_total_projects
     @total_projects
   end

   def get_total_ncloc
     @total_ncloc
   end

end