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

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Project;

import org.sonar.plugins.technicaldebt.axis.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@inheritDoc}
 */
public final class TechnicalDebtDecorator implements Decorator {

  private List<AxisDebtCalculator> axisList;
  private Configuration configuration;

  /**
   * {@inheritDoc}
   */
  public TechnicalDebtDecorator(Configuration configuration) {

    this.configuration = configuration;
    axisList = Arrays.asList(
      new CommentDebtCalculator(configuration),
      new ComplexityDebtCalculator(configuration),
      new CoverageDebtCalculator(configuration),
      new DuplicationDebtCalculator(configuration),
      new ViolationsDebtCalculator(configuration),
      new DesignDebtCalculator(configuration)
    );
  }

  /**
   * {@inheritDoc}
   */
  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  @DependsUpon
  public List<Metric> dependsOnMetrics() {
    List<Metric> list = new ArrayList<Metric>();
    for (AxisDebtCalculator axis : axisList) {
      list.addAll(axis.dependsOn());
    }
    return list;
  }

  @DependedUpon
  public List<Metric> generatesMetrics() {
    return Arrays.asList(
      TechnicalDebtMetrics.TECHNICAL_DEBT,
      TechnicalDebtMetrics.TECHNICAL_DEBT_DAYS,
      TechnicalDebtMetrics.TECHNICAL_DEBT_RATIO,
      TechnicalDebtMetrics.TECHNICAL_DEBT_REPARTITION);
  }

  /**
   * {@inheritDoc}
   */
  public void decorate(Resource resource, DecoratorContext context) {
    double sonarDebt = 0;
    double denominatorDensity = 0;
    PropertiesBuilder<String, Double> techDebtRepartition = new PropertiesBuilder<String, Double>(TechnicalDebtMetrics.TECHNICAL_DEBT_REPARTITION);

    // We calculate the total absolute debt and total maximum debt
    for (AxisDebtCalculator axis : axisList) {
      sonarDebt += axis.calculateAbsoluteDebt(context);
      denominatorDensity += axis.calculateTotalPossibleDebt(context);
    }

    // Then we calculate the % of each axis for this debt
    for (AxisDebtCalculator axis : axisList) {
      addToRepartition(techDebtRepartition, axis.getName(), axis.calculateAbsoluteDebt(context) / sonarDebt * 100);
    }

    double dailyRate = Double.valueOf(configuration.getString(TechnicalDebtPlugin.TD_DAILY_RATE, TechnicalDebtPlugin.TD_DAILY_RATE_DEFAULT));


    saveMeasure(context, TechnicalDebtMetrics.TECHNICAL_DEBT, sonarDebt * dailyRate);
    saveMeasure(context, TechnicalDebtMetrics.TECHNICAL_DEBT_DAYS, sonarDebt);
    if (denominatorDensity != 0) {
      saveMeasure(context, TechnicalDebtMetrics.TECHNICAL_DEBT_RATIO, sonarDebt / denominatorDensity * 100);
    }
    context.saveMeasure(techDebtRepartition.build());
  }

  private void saveMeasure(DecoratorContext decoratorContext, Metric metric, double measure) {
    if (measure * 10 > 5) {
      decoratorContext.saveMeasure(metric, measure);
    }
  }

  private void addToRepartition(PropertiesBuilder<String, Double> techDebtRepartition, String key, double value) {
    if (value > 0d) {
      // Math.floor is important to avoid getting very long doubles... see SONAR-859
      techDebtRepartition.add(key, Math.floor(value * 100.0) / 100);
    }
  }
}
