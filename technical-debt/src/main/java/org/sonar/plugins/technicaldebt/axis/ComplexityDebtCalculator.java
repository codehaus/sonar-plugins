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

import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.plugins.technicaldebt.TechnicalDebtMetrics;
import org.sonar.plugins.technicaldebt.TechnicalDebtPlugin;

import java.util.Arrays;
import java.util.List;

/**
 * {@inheritDoc}
 */
public final class ComplexityDebtCalculator extends AxisDebtCalculator {

  private boolean isJava;

  public ComplexityDebtCalculator(Settings settings, Project project) {
    super(settings);
    isJava = project.getLanguage() != null ? "java".equals(project.getLanguage().getKey()) : false;
  }

  /**
   * {@inheritDoc}
   */
  public double calculateAbsoluteDebt(DecoratorContext context) {
    return MeasureUtils.getValue(context.getMeasure(TechnicalDebtMetrics.TECHNICAL_DEBT_COMPLEXITY), 0.0) / HOURS_PER_DAY;
  }

  public double calculateTotalPossibleDebt(DecoratorContext context) {
    Measure classes = context.getMeasure((isJava ? CoreMetrics.CLASSES : CoreMetrics.FILES));
    Measure functions = context.getMeasure(CoreMetrics.FUNCTIONS);

    // FIXME Why no settings.getDouble() ?
    double debt = MeasureUtils.hasValue(classes) ? classes.getValue()
      * Double.valueOf(settings.getString(TechnicalDebtPlugin.COST_CLASS_COMPLEXITY)) : 0;
    debt += MeasureUtils.hasValue(functions) ? functions.getValue()
      * Double.valueOf(settings.getString(TechnicalDebtPlugin.COST_METHOD_COMPLEXITY)) : 0;

    // technicaldebt is calculated in man days
    return debt / HOURS_PER_DAY;
  }

  public List<Metric> dependsOn() {
    return Arrays.asList(CoreMetrics.CLASSES, CoreMetrics.FILES, CoreMetrics.FUNCTIONS, TechnicalDebtMetrics.TECHNICAL_DEBT_COMPLEXITY);
  }

  public String getName() {
    return "Complexity";
  }
}
