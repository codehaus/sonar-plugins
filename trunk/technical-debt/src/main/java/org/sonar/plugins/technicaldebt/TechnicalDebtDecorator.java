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
package org.sonar.plugins.technicaldebt;

import org.apache.commons.configuration.Configuration;

import org.sonar.api.core.CoreMetrics;

import org.sonar.api.batch.measures.Measure;
import org.sonar.api.batch.measures.PropertiesBuilder;
import org.sonar.api.batch.measures.MeasureUtils;

import org.sonar.api.batch.ResourceUtils;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependsOn;
import org.sonar.api.batch.Generates;
import org.sonar.api.batch.Project;
import org.sonar.api.batch.Resource;

import org.sonar.api.utils.KeyValueFormat;

import org.sonar.commons.Metric;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 */
public class TechnicalDebtDecorator implements Decorator {

  private static final double HOURS_PER_DAY = 8.0;

  // Those 2 values cannot be changed too quickly... has to be one of the value we keep in DB
  private static final int MAX_COMPLEXITY_CLASS = 60;
  private static final int MAX_COMPLEXITY_METHOD = 8;
  private static final double COVERAGE_TARGET = 0.8;

  private final Configuration configuration;

  /**
   * {@inheritDoc}
   */
  public TechnicalDebtDecorator(Configuration configuration) {
    this.configuration = configuration;
  }

  @DependsOn
  public List<Metric> dependsOnMetrics() {
    List<Metric> metrics = new ArrayList<Metric>();
    metrics.add(CoreMetrics.DUPLICATED_BLOCKS);
    metrics.add(CoreMetrics.VIOLATIONS);
    metrics.add(CoreMetrics.INFO_VIOLATIONS);
    metrics.add(CoreMetrics.PUBLIC_UNDOCUMENTED_API);
    metrics.add(CoreMetrics.UNCOVERED_COMPLEXITY_BY_TESTS);
    metrics.add(CoreMetrics.CLASS_COMPLEXITY_DISTRIBUTION);
    metrics.add(CoreMetrics.COMPLEXITY);
    metrics.add(CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION);
    return metrics;
  }

  @Generates
  public List<Metric> generatesMetrics() {
    List<Metric> metrics = new ArrayList<Metric>();
    metrics.add(TechnicalDebtMetrics.TECHNICAL_DEBT);
    metrics.add(TechnicalDebtMetrics.TECHNICAL_DEBT_DAYS);
    metrics.add(TechnicalDebtMetrics.TECHNICAL_DEBT_REPARTITION);
    return metrics;
  }

  /**
   * {@inheritDoc}
   */
  public boolean shouldDecorateProject(Project project) {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  public void decorate(Resource resource, DecoratorContext decoratorContext) {
    double duplicationsDebt = calculateDuplicationDebt(decoratorContext);
    double violationsDebt = calculateViolationsDebt(decoratorContext);
    double commentsDebt = calculateCommentsDebt(decoratorContext);
    double coverageDebt = calculateCoverageDebt(decoratorContext);
    double complexityDebt = calculateComplexityDebt(decoratorContext);

    Measure debtRepartition = calculateDebtRepartition(duplicationsDebt, violationsDebt, commentsDebt, coverageDebt, complexityDebt).build();
    double sonarDebt = duplicationsDebt + violationsDebt + commentsDebt + coverageDebt + complexityDebt;

    double dailyRate = getWeight(TechnicalDebtPlugin.TD_DAILY_RATE, TechnicalDebtPlugin.TD_DAILY_RATE_DEFAULT);

    decoratorContext.saveMeasure(TechnicalDebtMetrics.TECHNICAL_DEBT, sonarDebt * dailyRate);
    decoratorContext.saveMeasure(TechnicalDebtMetrics.TECHNICAL_DEBT_DAYS, sonarDebt);
    decoratorContext.saveMeasure(debtRepartition);
  }

  // Calculates the technical debt due on coverage (in man days)
  private double calculateCoverageDebt(DecoratorContext decoratorContext) {
    Measure measure = decoratorContext.getMeasure(CoreMetrics.UNCOVERED_COMPLEXITY_BY_TESTS);

    if (!MeasureUtils.hasValue(measure)) {
      return 0.0;
    }

    // It is not reasonable to have an objective at 100%, so target is 80% for coverage
    double reasonableObjective = (1 - COVERAGE_TARGET) * decoratorContext.getMeasure(CoreMetrics.COMPLEXITY).getValue();
    double uncovComplexityGap = measure.getValue() - reasonableObjective;

    // technicaldebt is calculate in man days
    return (uncovComplexityGap > 0.0 ? uncovComplexityGap : 0.0) * getWeight(TechnicalDebtPlugin.TD_COST_UNCOVERED_COMPLEXITY, TechnicalDebtPlugin.TD_COST_UNCOVERED_COMPLEXITY_DEFAULT) / HOURS_PER_DAY;
  }

  // Calculates the technical technicaldebt due on comments (in man days)
  private double calculateCommentsDebt(DecoratorContext decoratorContext) {
    Measure measure = decoratorContext.getMeasure(CoreMetrics.PUBLIC_UNDOCUMENTED_API);

    if (!MeasureUtils.hasValue(measure)) {
      return 0.0;
    }
    // technicaldebt is calculate in man days
    return measure.getValue() * getWeight(TechnicalDebtPlugin.TD_COST_UNDOCUMENTED_API, TechnicalDebtPlugin.TD_COST_UNDOCUMENTED_API_DEFAULT) / HOURS_PER_DAY;
  }

  // Calculates the technical technicaldebt due on coding rules violations (in man days)
  private double calculateViolationsDebt(DecoratorContext decoratorContext) {
    Measure mViolations = decoratorContext.getMeasure(CoreMetrics.VIOLATIONS);
    Measure mInfoViolations = decoratorContext.getMeasure(CoreMetrics.INFO_VIOLATIONS);

    double violations =   (MeasureUtils.hasValue(mViolations) ? mViolations.getValue() : 0.0)
                        - (MeasureUtils.hasValue(mInfoViolations)  ? mInfoViolations.getValue() : 0.0);

    // technicaldebt is calculate in man days
    return violations * getWeight(TechnicalDebtPlugin.TD_COST_VIOLATION, TechnicalDebtPlugin.TD_COST_VIOLATION_DEFAULT) / HOURS_PER_DAY;
  }

  // Calculates the technical technicaldebt due on duplication (in man days)
  private double calculateDuplicationDebt(DecoratorContext decoratorContext) {
    Measure measure = decoratorContext.getMeasure(CoreMetrics.DUPLICATED_BLOCKS);

    if (!MeasureUtils.hasValue(measure)) {
      return 0.0;
    }
    // technicaldebt is calculate in man days
    return measure.getValue() * getWeight(TechnicalDebtPlugin.TD_COST_DUPLI_BLOCK, TechnicalDebtPlugin.TD_COST_DUPLI_BLOCK_DEFAULT) / HOURS_PER_DAY;
  }

  // Calculates the technical technicaldebt due on complexity (in man days)
  private double calculateComplexityDebt(DecoratorContext decoratorContext) {
    // First, the classes that have high complexity
    int nbClassToSplit = 0;
    if (ResourceUtils.isFile(decoratorContext.getResource())) {
      Measure complexity = decoratorContext.getMeasure(CoreMetrics.COMPLEXITY);

      if (MeasureUtils.hasValue(complexity) && complexity.getValue() >= MAX_COMPLEXITY_CLASS) {
        nbClassToSplit = 1;
      }
    } else {
      nbClassToSplit = getClassAboveMaxComplexity(decoratorContext);
    }

    // Then, the methods that have high complexity
    int nbMethodsToSplit = getMethodsAboveMaxComplexity(decoratorContext);

    // Finally, we sum the 2
    double debt = nbClassToSplit * getWeight(TechnicalDebtPlugin.TD_COST_COMP_CLASS, TechnicalDebtPlugin.TD_COST_COMP_CLASS_DEFAULT);
    debt += nbMethodsToSplit * getWeight(TechnicalDebtPlugin.TD_COST_COMP_METHOD, TechnicalDebtPlugin.TD_COST_COMP_METHOD_DEFAULT);

    // technicaldebt is calculated in man days
    return debt / HOURS_PER_DAY;
  }

  private int getMethodsAboveMaxComplexity(DecoratorContext decoratorContext) {
    Measure methodComplexity = decoratorContext.getMeasure(CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION);

    if (!MeasureUtils.hasValue(methodComplexity)) {
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

    if (!MeasureUtils.hasValue(classComplexity)) {
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

  // Computes the repartition of the technicaldebt
  private PropertiesBuilder calculateDebtRepartition(double duplicationDebt, double violationsDebt, double commentsDebt, double coverageDebt, double complexityDebt) {
    PropertiesBuilder<String, Double> techDebtRepartition = new PropertiesBuilder<String, Double>(TechnicalDebtMetrics.TECHNICAL_DEBT_REPARTITION);
    // Math.floor is important to avoid getting very long doubles... see SONAR-859
    techDebtRepartition.add("Violations", Math.floor(violationsDebt));
    techDebtRepartition.add("Duplication", Math.floor(duplicationDebt));
    techDebtRepartition.add("Comments", Math.floor(commentsDebt));
    techDebtRepartition.add("Coverage", Math.floor(coverageDebt));
    techDebtRepartition.add("Complexity", Math.floor(complexityDebt));
    return techDebtRepartition;
  }

  private double getWeight(String keyWeight, String defaultWeight) {
    Object property = configuration.getProperty(keyWeight);
    if (property != null) {
      return Double.parseDouble((String) property);
    }
    return Double.parseDouble(defaultWeight);
  }
}
