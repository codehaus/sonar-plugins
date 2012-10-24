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

package org.sonar.plugins.qi;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;

import java.util.Arrays;
import java.util.List;

@Properties({
  @Property(key = QIPlugin.QI_CODING_PRIORITY_WEIGHTS, defaultValue = QIPlugin.QI_CODING_PRIORITY_WEIGHTS_DEFAULT,
    name = "A weight is associated to each CODING violation, depending on the priority", description = ""),

  @Property(key = QIPlugin.QI_STYLE_PRIORITY_WEIGHTS, defaultValue = QIPlugin.QI_STYLE_PRIORITY_WEIGHTS_DEFAULT,
    name = "A weight is associated to each STYLE violation, depending on the priority", description = ""),

  @Property(key = QIPlugin.QI_CODING_AXIS_WEIGHT, defaultValue = QIPlugin.QI_CODING_AXIS_WEIGHT_DEFAULT,
    name = "The weight the CODING axis is given", description = "", type = PropertyType.FLOAT),

  @Property(key = QIPlugin.QI_STYLE_AXIS_WEIGHT, defaultValue = QIPlugin.QI_STYLE_AXIS_WEIGHT_DEFAULT,
    name = "The weight the STYLE axis is given", description = "", type = PropertyType.FLOAT),

  @Property(key = QIPlugin.QI_COMPLEXITY_AXIS_WEIGHT, defaultValue = QIPlugin.QI_COMPLEXITY_AXIS_WEIGHT_DEFAULT,
    name = "The weight the COMPLEXITY axis is given", description = "", type = PropertyType.FLOAT),

  @Property(key = QIPlugin.QI_COVERAGE_AXIS_WEIGHT, defaultValue = QIPlugin.QI_COVERAGE_AXIS_WEIGHT_DEFAULT,
    name = "The weight the COVERAGE axis is given", description = "", type = PropertyType.FLOAT)
})
public class QIPlugin extends SonarPlugin {
  /**
   * The violation weights for each priority on the coding axis
   */
  public static final String QI_CODING_PRIORITY_WEIGHTS = "qi.coding.priority.weights";

  /**
   * The default violation weights for each priority on the coding axis
   */
  public static final String QI_CODING_PRIORITY_WEIGHTS_DEFAULT = "INFO=1;MINOR=1;MAJOR=3;CRITICAL=5;BLOCKER=10";

  /**
   * The violation weights for each priority on the style axis
   */
  public static final String QI_STYLE_PRIORITY_WEIGHTS = "qi.style.priority.weights";

  /**
   * The default violation weights for each priority on the style axis
   */
  public static final String QI_STYLE_PRIORITY_WEIGHTS_DEFAULT = "INFO=1;MINOR=1;MAJOR=1;CRITICAL=10;BLOCKER=10";

  /**
   * The weight of the coding axis
   */
  public static final String QI_CODING_AXIS_WEIGHT = "qi.coding.axis.weights";

  /**
   * The default weight of the coding axis
   */
  public static final String QI_CODING_AXIS_WEIGHT_DEFAULT = "4.5";

  /**
   * The weight of the style axis
   */
  public static final String QI_STYLE_AXIS_WEIGHT = "qi.style.axis.weights";

  /**
   * The default weight of the style axis
   */
  public static final String QI_STYLE_AXIS_WEIGHT_DEFAULT = "1.5";

  /**
   * The weight of the complexity axis
   */
  public static final String QI_COMPLEXITY_AXIS_WEIGHT = "qi.complexity.axis.weights";

  /**
   * The default weight of the complexity axis
   */
  public static final String QI_COMPLEXITY_AXIS_WEIGHT_DEFAULT = "2.0";

  /**
   * The weight of the coverage axis
   */
  public static final String QI_COVERAGE_AXIS_WEIGHT = "qi.coverage.axis.weights";

  /**
   * The default weight of the coverage axis
   */
  public static final String QI_COVERAGE_AXIS_WEIGHT_DEFAULT = "2.0";

  /**
   * The complexity ranges bottom limits
   */
  public static final Number[] COMPLEXITY_BOTTOM_LIMITS = {30, 20, 10, 2, 1};

  /**
   * @return the list of extensions of the plugin
   */
  public List getExtensions() {
    return Arrays.asList(CodingViolationsDecorator.class, ComplexityDistributionDecorator.class, ComplexityDecorator.class,
      CoverageDecorator.class, QIDecorator.class, QIMetrics.class, QIWidget.class, StyleViolationsDecorator.class);
  }

}
