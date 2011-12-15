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
# License along with Sonar; if nScaleRadiatorWebServiceControllerot, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
#
require "json"
class Api::RadiatorGradientWebServiceController < Api::RestController

  def initialize()
    @min_color = Color::RGB.from_html(Property.value('sonar.radiator.minColor', nil, 'EE0000'))
    @mean_color = Color::RGB.from_html(Property.value('sonar.radiator.meanColor', nil, 'FFEE00'))
    @max_color = Color::RGB.from_html(Property.value('sonar.radiator.maxColor', nil, '00AA00'))
    @nb_slices = 20
  end
  
  private
  
  def rest_call
    rest_render({:colors => get_color_array, :values => get_value_array})  
  end

  def rest_to_json(objects)
    JSON({
        :label => ['gradient'],
        :color => objects[:colors],
        :values => [
            {
              :label => 'gradient',
              :values => objects[:values]
            }
        ]
    })

  end

  def get_color_array
    color_array = []  
    for i in 0..@nb_slices
       color_array.push(@max_color.mix_with(@mean_color, (50.0 - i.to_f * (50.0 / @nb_slices.to_f)) * 2.0).html)
    end
    for i in 1..@nb_slices
       color_array.push(@min_color.mix_with(@mean_color, (50.0 - (@nb_slices.to_f - i.to_f) * (50.0 / @nb_slices.to_f)) * 2.0).html)
    end
    return color_array
  end

  def get_value_array
    value_array = []  
    for i in 0..@nb_slices
       value_array.push(1)
    end
    for i in 1..@nb_slices
       value_array.push(1)
    end
    return value_array
  end

end