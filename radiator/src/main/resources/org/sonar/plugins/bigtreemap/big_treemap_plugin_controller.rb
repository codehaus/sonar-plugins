#
# Sonar, open source software quality management tool.
# Copyright (C) 2009 SonarSource SA
# mailto:contact AT sonarsource DOT com
#
# Sonar is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 3 of the License, or (at your option) any later version.
#
# Sonar is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with Sonar; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
#
class Api::RubyBigTreemapWebServiceController < Api::ResourceRestController

  private
  
  def rest_call
    parent_resource_key = nil
    if @resource
      snapshot = @resource.last_snapshot
      snapshots = snapshot.children.select {|s| s.qualifier!=Snapshot::QUALIFIER_UNIT_TEST_CLASS}
      parent_snapshot = snapshot.parent
      parent_resource_key = parent_snapshot.project.key if parent_snapshot
    else
      snapshots = Snapshot.last_authorized_enabled_projects(current_user)
    end
    
    size_metric_key = params[:size]
    color_metric_key = params[:color]

    size_metric = Metric.by_key(size_metric_key) || Sonar::TreemapBuilder.default_size_metric
    color_metric = Metric.by_key(color_metric_key) || Sonar::TreemapBuilder.default_color_metric

    if snapshots.empty?
      measures = []
    else
      # temporary fix for SONAR-1098
      snapshots = snapshots[0...999]
      measures = ProjectMeasure.find(:all,
        :conditions => ['rules_category_id IS NULL and rule_id IS NULL and rule_priority IS NULL and metric_id IN (?) and snapshot_id IN (?)',
          [size_metric.id, color_metric.id], snapshots.map{|s| s.id}])
    end
    measures_by_snapshot = Sonar::TreemapBuilder.measures_by_snapshot(snapshots, measures)
    rest_render({:snapshots => snapshots, :measures_by_snapshot => measures_by_snapshot, :size => size_metric, :color => color_metric,
      :params => params, :parent_resource_key => parent_resource_key})
  
  end

  def rest_to_json(objects)
    measures_by_snapshot = objects[:measures_by_snapshot]
    snapshots = objects[:snapshots]
    params = objects[:params]
    parent_resource_key = objects[:parent_resource_key]
      
    size_metric = objects[:size]
    color_metric = objects[:color]

    children = [];
    area = 0
    snapshots.each do |snapshot|
      measures = measures_by_snapshot[snapshot]
      if measures
        size = get_measure(size_metric, measures)
        color = get_measure(color_metric, measures)
        if size
          children << {:children => [], :data => {'$color' => get_measure_value(color), '$area' => get_measure_value(size),
                                                    :size_frmt => get_measure_value_frmt(size), :color_frmt => get_measure_value_frmt(color)}, 
                                        :id => snapshot.project.key , :name => snapshot.project.name}
          area = area + get_measure_value(size)
        end
      end
    end
    
    {:children => children, :data => { '$area' => area }, :id => "radiator", :name => "Radiator", :parent => parent_resource_key, 
      :size_metric => size_metric.short_name, :color_metric => color_metric.short_name, :color_metric_direction => color_metric.direction}.to_json
  end
  
  def get_measure(metric, measures)
    measures.find do |measure|
      measure.metric_id==metric.id && measure.rule_id.nil? && measure.rules_category_id.nil? && measure.rule_priority.nil?
    end
  end
  
  def get_measure_value_frmt(measure)
    measure.nil? ? "None" : measure.formatted_value
  end
  
  def get_measure_value(measure)
    value = nil
    if measure
      if measure.metric.value_type==Metric::VALUE_TYPE_LEVEL
        case(measure.text_value)
          when Metric::TYPE_LEVEL_OK : value=100
          when Metric::TYPE_LEVEL_WARN : value=50
          when Metric::TYPE_LEVEL_ERROR : value=0
        end
      else
        value = measure.value
      end
    end
    value
  end

end