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
package org.sonar.plugins.technicaldebt.axis;

import org.apache.commons.configuration.Configuration;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.plugins.technicaldebt.TechnicalDebtPlugin;

import java.util.Arrays;
import java.util.List;


public class CoverageDebtCalculator extends AxisDebtCalculator {

  private static final double COVERAGE_TARGET = 0.8;

  public CoverageDebtCalculator(Configuration configuration) {
    super(configuration);
  }

  public double calculateAbsoluteDebt(DecoratorContext context) {
    Measure measure = context.getMeasure(CoreMetrics.UNCOVERED_COMPLEXITY_BY_TESTS);

    if (!MeasureUtils.hasValue(measure)) {
      return 0.0;
    }

    // It is not reasonable to have an objective at 100%, so target is 80% for coverage
    double reasonableObjective = (1 - COVERAGE_TARGET) * context.getMeasure(CoreMetrics.COMPLEXITY).getValue();
    double uncovComplexityGap = measure.getValue() - reasonableObjective;

    // technicaldebt is calculate in man days
    return (uncovComplexityGap > 0.0 ? uncovComplexityGap : 0.0) * getWeight(TechnicalDebtPlugin.TD_COST_UNCOVERED_COMPLEXITY, TechnicalDebtPlugin.TD_COST_UNCOVERED_COMPLEXITY_DEFAULT) / HOURS_PER_DAY;
  }


  public double calculateDebtForRatio(DecoratorContext context) {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public double calculateTotalPossibleDebt(DecoratorContext context) {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public List<Metric> dependsOn() {
    return Arrays.asList(CoreMetrics.UNCOVERED_COMPLEXITY_BY_TESTS);
  }

  public String getName() {
    return "Coverage";

  }
}
