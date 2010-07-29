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

package org.sonar.plugins.technicaldebt.axis;

import org.apache.commons.configuration.Configuration;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.utils.KeyValueFormat;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.plugins.technicaldebt.TechnicalDebtPlugin;
import org.sonar.plugins.technicaldebt.TechnicalDebtMetrics;

import java.util.Map;
import java.util.List;
import java.util.Arrays;

/**
 * {@inheritDoc}
 */
public final class ComplexityDebtCalculator extends AxisDebtCalculator {

  public ComplexityDebtCalculator(Configuration configuration) {
    super(configuration);
  }

  /**
   * {@inheritDoc}
   */
  public double calculateAbsoluteDebt(DecoratorContext context) {
    Measure complexity = context.getMeasure(TechnicalDebtMetrics.TECHNICAL_DEBT_COMPLEXITY);
     if (!MeasureUtils.hasData(complexity)) {
       return 0.0;
     }
    Map<String, Double> complexityDistribution = KeyValueFormat.parse(complexity.getData(), new KeyValueFormat.StringNumberPairTransformer());
    int nbClassToSplit = complexityDistribution.get("CLASS").intValue();
    int nbMethodsToSplit = complexityDistribution.get("METHOD").intValue();

    double debt = nbClassToSplit * getWeight(TechnicalDebtPlugin.TD_COST_COMP_CLASS, TechnicalDebtPlugin.TD_COST_COMP_CLASS_DEFAULT);
    debt += nbMethodsToSplit * getWeight(TechnicalDebtPlugin.TD_COST_COMP_METHOD, TechnicalDebtPlugin.TD_COST_COMP_METHOD_DEFAULT);

    // technicaldebt is calculated in man days
    return debt / HOURS_PER_DAY;
  }

  public double calculateTotalPossibleDebt(DecoratorContext context) {
    Measure files = context.getMeasure(CoreMetrics.CLASSES);
    Measure functions = context.getMeasure(CoreMetrics.FUNCTIONS);

    double debt = MeasureUtils.hasValue(files) ? files.getValue() * getWeight(TechnicalDebtPlugin.TD_COST_COMP_CLASS, TechnicalDebtPlugin.TD_COST_COMP_CLASS_DEFAULT) : 0;
    debt += MeasureUtils.hasValue(functions) ? functions.getValue() * getWeight(TechnicalDebtPlugin.TD_COST_COMP_METHOD, TechnicalDebtPlugin.TD_COST_COMP_METHOD_DEFAULT) : 0;

    // technicaldebt is calculated in man days
    return debt / HOURS_PER_DAY;
  }

  public List<Metric> dependsOn() {
    return Arrays.asList(CoreMetrics.CLASSES, CoreMetrics.FUNCTIONS, TechnicalDebtMetrics.TECHNICAL_DEBT_COMPLEXITY);
  }

  public String getName() {
    return "Complexity";
  }

}
