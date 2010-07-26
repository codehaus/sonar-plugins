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

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import java.util.Arrays;
import java.util.List;

/**
 * The decorator implementation  to calculate the Quality Index
 */
public class QIDecorator implements Decorator {

  /**
   * All 4 axes should be calculated before QI computation
   *
   * @return the 4 metrics
   */
  @DependsUpon
  public List<Metric> getRequiredMetrics() {
    return Arrays.asList(QIMetrics.QI_COMPLEXITY, QIMetrics.QI_TEST_COVERAGE,
        QIMetrics.QI_CODING_VIOLATIONS, QIMetrics.QI_STYLE_VIOLATIONS);
  }

  /**
   * @return the QI metric
   */
  @DependedUpon
  public List<Metric> getGeneratedMetrics() {
    return Arrays.asList(QIMetrics.QI_QUALITY_INDEX);
  }

  /**
   * @param project the project
   * @return whether to execute the decorator on the project
   */
  public boolean shouldExecuteOnProject(Project project) {
    return Utils.shouldExecuteOnProject(project);
  }

  /**
   * The decorate action
   *
   * @param resource the resource
   * @param context  the context
   */
  public void decorate(Resource resource, DecoratorContext context) {
    if (Utils.shouldSaveMeasure(resource)) {
      double value = 10.0;
      for (Metric metric : getRequiredMetrics()) {
        value -= MeasureUtils.getValue(context.getMeasure(metric), 0.0);
      }
      context.saveMeasure(QIMetrics.QI_QUALITY_INDEX, Math.max(value, 0.0));
    }
  }
}
