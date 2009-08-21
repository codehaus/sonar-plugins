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
import org.sonar.api.utils.KeyValueFormat;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.plugins.technicaldebt.TechnicalDebtPlugin;

import java.util.Map;
import java.util.List;
import java.util.Arrays;

/**
 * {@inheritDoc}
 */
public final class ComplexityDebtCalculator extends AxisDebtCalculator {

  // Those 2 values cannot be changed too quickly... has to be one of the value we keep in DB
  private static final int MAX_COMPLEXITY_CLASS = 60;
  private static final int MAX_COMPLEXITY_METHOD = 8;

  /**
   * {@inheritDoc}
   */
  public ComplexityDebtCalculator(Configuration configuration) {
    super(configuration);
  }

  /**
   * {@inheritDoc}
   */
  public double calculateAbsoluteDebt(DecoratorContext context) {
    // First, the classes that have high complexity
    int nbClassToSplit = 0;
    if (ResourceUtils.isFile(context.getResource())) {
      Measure complexity = context.getMeasure(CoreMetrics.COMPLEXITY);

      if (MeasureUtils.hasValue(complexity) && complexity.getValue() >= MAX_COMPLEXITY_CLASS) {
        nbClassToSplit = 1;
      }
    } else {
      nbClassToSplit = getClassAboveMaxComplexity(context);
    }

    // Then, the methods that have high complexity
    int nbMethodsToSplit = getMethodsAboveMaxComplexity(context);

    // Finally, we sum the 2
    double debt = nbClassToSplit * getWeight(TechnicalDebtPlugin.TD_COST_COMP_CLASS, TechnicalDebtPlugin.TD_COST_COMP_CLASS_DEFAULT);
    debt += nbMethodsToSplit * getWeight(TechnicalDebtPlugin.TD_COST_COMP_METHOD, TechnicalDebtPlugin.TD_COST_COMP_METHOD_DEFAULT);

    // technicaldebt is calculated in man days
    return debt / HOURS_PER_DAY;
  }

  private int getMethodsAboveMaxComplexity(DecoratorContext decoratorContext) {
    Measure methodComplexity = decoratorContext.getMeasure(CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION);

    if (!MeasureUtils.hasData(methodComplexity)) {
      return 0;
    }

    int nb = 0;
    Map<String, String> distribution = KeyValueFormat.parse(methodComplexity.getData());

    for (String key : distribution.keySet()) {
      if (Integer.parseInt(key) >= MAX_COMPLEXITY_METHOD) {
        nb += Integer.parseInt(distribution.get(key));
      }
    }
    return nb;
  }

  private int getClassAboveMaxComplexity(DecoratorContext decoratorContext) {
    Measure classComplexity = decoratorContext.getMeasure(CoreMetrics.CLASS_COMPLEXITY_DISTRIBUTION);

    if (!MeasureUtils.hasData(classComplexity)) {
      return 0;
    }

    int nb = 0;
    Map<String, String> distribution = KeyValueFormat.parse(classComplexity.getData());

    for (String key : distribution.keySet()) {
      if (Integer.parseInt(key) >= MAX_COMPLEXITY_CLASS) {
        nb += Integer.parseInt(distribution.get(key));
      }
    }
    return nb;
  }

  /**
   * {@inheritDoc}
   */
  public double calculateTotalPossibleDebt(DecoratorContext context) {
    Measure files = context.getMeasure(CoreMetrics.CLASSES);
    Measure functions = context.getMeasure(CoreMetrics.FUNCTIONS);

    double debt = MeasureUtils.hasValue(files) ? files.getValue() * getWeight(TechnicalDebtPlugin.TD_COST_COMP_CLASS, TechnicalDebtPlugin.TD_COST_COMP_CLASS_DEFAULT) : 0;
    debt += MeasureUtils.hasValue(functions) ? functions.getValue() * getWeight(TechnicalDebtPlugin.TD_COST_COMP_METHOD, TechnicalDebtPlugin.TD_COST_COMP_METHOD_DEFAULT) : 0;

    // technicaldebt is calculated in man days
    return debt / HOURS_PER_DAY;
  }

  /**
   * {@inheritDoc}
   */
  public List<Metric> dependsOn() {
    return Arrays.asList(CoreMetrics.COMPLEXITY, CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION,
      CoreMetrics.CLASS_COMPLEXITY_DISTRIBUTION, CoreMetrics.CLASSES, CoreMetrics.FUNCTIONS);
  }

  /**
   * {@inheritDoc}
   */
  public String getName() {
    return "Complexity";
  }

}
