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
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Resource;

import java.util.Arrays;
import java.util.List;

/**
 * An implementation of AbstractDecorator to measure the QI coverage axis
 */
public class CoverageDecorator extends AbstractDecorator {

  /**
   * Creates a CoverageDecorator
   *
   * @param configuration the config
   */
  public CoverageDecorator(Configuration configuration) {
    super(configuration, QIMetrics.QI_TEST_COVERAGE,
        QIPlugin.QI_COVERAGE_AXIS_WEIGHT, QIPlugin.QI_COVERAGE_AXIS_WEIGHT_DEFAULT);
  }

  /**
   * The coverage must be computed before we can start decorating...
   *
   * @return the list of dependency
   */
  @Override
  public List<Metric> dependsUpon() {
    return Arrays.asList(CoreMetrics.LINES_TO_COVER, CoreMetrics.UNCOVERED_LINES);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    if (Utils.shouldSaveMeasure(resource)) {
      saveMeasure(context, computeCoverage(context));
    }
  }

  protected double computeCoverage(DecoratorContext context) {
    double linesToCover = MeasureUtils.getValue(context.getMeasure(CoreMetrics.LINES_TO_COVER), 0.0);
    double uncoveredLines = MeasureUtils.getValue(context.getMeasure(CoreMetrics.UNCOVERED_LINES), 0.0);

    double coveredLines = linesToCover - uncoveredLines;
    return 1 - (coveredLines / getValidLines(context));
  }
}
