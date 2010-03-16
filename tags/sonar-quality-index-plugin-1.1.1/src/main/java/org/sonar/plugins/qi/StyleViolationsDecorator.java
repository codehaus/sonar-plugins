/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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

import org.apache.commons.configuration.Configuration;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Metric;

public class StyleViolationsDecorator extends AbstractViolationsDecorator {

  /**
   * Creates a StyleViolationsDecorator, i.e. implements an AbstractViolationsDecorator
   * to decorate the style axis of the QI
   *
   * @param configuration the configuration
   */
  public StyleViolationsDecorator(Configuration configuration) {
    super(configuration, QIMetrics.QI_STYLE_VIOLATIONS,
        QIPlugin.QI_STYLE_AXIS_WEIGHT, QIPlugin.QI_STYLE_AXIS_WEIGHT_DEFAULT);
  }


  /**
   * Multiplies the "normal" valid lines by 10 to decrease the effect of style errors
   *
   * @param context the context
   * @return the valid lines
   */
  @Override
  public double getValidLines(DecoratorContext context) {
    return super.getValidLines(context) * 10;
  }

  /**
   * @return the style axis weight config key
   */
  @Override
  public String getConfigurationKey() {
    return QIPlugin.QI_STYLE_PRIORITY_WEIGHTS;
  }

  /**
   * @return the style axis default weight config key
   */
  @Override
  public String getDefaultConfigurationKey() {
    return QIPlugin.QI_STYLE_PRIORITY_WEIGHTS_DEFAULT;
  }

  /**
   * @return the metric to store the style weighted violations
   */
  public Metric getWeightedViolationMetricKey() {
    return QIMetrics.QI_STYLE_WEIGHTED_VIOLATIONS;
  }

  /**
   * @return the Checkstyle key
   */
  @Override
  public String getPluginKey() {
    return CoreProperties.CHECKSTYLE_PLUGIN;
  }
}
