/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.radiator;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;

import java.util.Arrays;
import java.util.List;

@Properties({
  @Property(key = "sonar.radiator.defaultSizeMetric",
    description = "See <a href='http://docs.codehaus.org/display/SONAR/Metric+definitions' target='_blank'>available metrics</a>.",
    name = "Default Size Metric",
    defaultValue = "ncloc",
    global = true, project = false, module = false),
  @Property(key = "sonar.radiator.defaultColorMetric",
    description = "See <a href='http://docs.codehaus.org/display/SONAR/Metric+definitions' target='_blank'>available metrics</a>.",
    name = "Default Color Metric",
    defaultValue = "violations_density",
    global = true, project = false, module = false),
  @Property(key = "sonar.radiator.minColor",
    name = "Min Color",
    defaultValue = "EE0000",
    global = true, project = false, module = false),
  @Property(key = "sonar.radiator.meanColor",
    name = "Mean Color",
    defaultValue = "FFEE00",
    global = true, project = false, module = false),
  @Property(key = "sonar.radiator.maxColor",
    name = "Max Color",
    defaultValue = "00AA00",
    global = true, project = false, module = false),
  @Property(key = "sonar.radiator.customThresholds",
    description = "Usage: metric_key1:worst_threshold1:best_threshold1,metric_key2:worst_threshold2:best_threshold2,...<br/>Example: violations_density:50:100,duplicated_lines_density:0:20",
    name = "Custom Thresholds",
    global = true, project = false, module = false)})
public final class RadiatorPlugin extends SonarPlugin {

  public List getExtensions() {
    return Arrays.asList(RadiatorPage.class, RadiatorWidget.class, RadiatorWebService.class, RadiatorGradientWebService.class);
  }

}
