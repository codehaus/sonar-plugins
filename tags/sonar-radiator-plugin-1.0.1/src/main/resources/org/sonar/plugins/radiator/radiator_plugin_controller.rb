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
require "json"
class Api::RadiatorWebServiceController < Api::RestController

  def initialize()
    @min = 0
    @max = 100
    @min_color = Color::RGB.from_html("DF0000")   # red
    @mean_color = Color::RGB.from_html("FFB000")   # orange
    @max_color = Color::RGB.from_html("00DF00")   # green
  end

  private
  
  def rest_call
    parent_resource_key=nil
    @resource=Project.by_key(params[:resource]) if params[:resource]

    if @resource
      last_snapshot=@resource.last_snapshot
      snapshots = Snapshot.find(:all,
        :conditions => ['parent_snapshot_id=? AND qualifier<>? AND status=?',last_snapshot.id, Snapshot::QUALIFIER_UNIT_TEST_CLASS, Snapshot::STATUS_PROCESSED],
        :include => 'project')
      parent_snapshot=last_snapshot.parent
      parent_resource_key = parent_snapshot.project_id if parent_snapshot 
    else
      snapshots = Snapshot.last_enabled_projects
    end

    snapshots=select_authorized(:user, snapshots)

    size_metric = Metric.by_key(params[:size]) || Sonar::TreemapBuilder.default_size_metric
    color_metric = Metric.by_key(params[:color]) || Sonar::TreemapBuilder.default_color_metric

    if snapshots.empty?
      measures = []
    else
      # WTF Oracle is #!*$$. It does not accept IN clause with more than 1000 elements
      snapshots = snapshots[0...999]
      measures = ProjectMeasure.find(:all,
        :conditions => ['rules_category_id IS NULL AND rule_id IS NULL AND rule_priority IS NULL AND metric_id IN (?) AND snapshot_id IN (?)',
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
          resource = snapshot.project
          is_file = resource.entity? && resource.copy_resource_id.nil?
          children << {:children => [], :data => {'$color' => get_hex_color(color, color_metric), '$area' => get_measure_value(size),
                                                    :size_frmt => get_measure_value_frmt(size), :color_frmt => get_measure_value_frmt(color)}, 
                                        :id => snapshot.project_id , :name => snapshot.project.name, :is_file => is_file}
          area = area + get_measure_value(size)
        end
      end
    end
    
    JSON({:children => children, :data => { '$area' => area }, :id => "radiator", :name => "Radiator", :parent => parent_resource_key, 
      :min => @min, :max => @max, :size_metric => size_metric.short_name, :color_metric => color_metric.short_name, :color_metric_direction => color_metric.direction})
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
        value = measure.value.to_f
      end
    end
    value
  end
  
  def get_hex_color(measure, color_metric)
    get_color(measure, color_metric).html
  end
  
  def get_color(measure, color_metric)
    if measure && !measure.alert_status.blank?
      case(measure.alert_status)
        when Metric::TYPE_LEVEL_OK : return Color::RGB.from_html("00DF00")
        when Metric::TYPE_LEVEL_ERROR : return Color::RGB.from_html("DF0000")
        when Metric::TYPE_LEVEL_WARN : return Color::RGB.from_html("ff8500")
      end
    end
    value = get_measure_value(measure)
    if value.nil?
      return Color::RGB.from_html("DDDDDD")
    end
    interval = (@max - @min)/2
    mean = (@min + @max) / 2.0
    if (value > mean)
      value_percent = ((value - mean) / interval) * 100.0
      color = @max_color.mix_with(@mean_color, value_percent) if color_metric.direction >= 0
      color = @min_color.mix_with(@mean_color, value_percent) if color_metric.direction < 0
    else
      value_percent = ((mean - value) / interval) * 100.0
      color = @min_color.mix_with(@mean_color, value_percent) if color_metric.direction >= 0
      color = @max_color.mix_with(@mean_color, value_percent) if color_metric.direction < 0
    end
    color
  end

end