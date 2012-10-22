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
import org.sonar.plugins.technicaldebt.TechnicalDebtPlugin;

import java.util.Arrays;
import java.util.List;

/**
 * {@inheritDoc}
 */
public final class DuplicationDebtCalculator extends AxisDebtCalculator {
  private static final int NUMBER_OF_LINES_PER_BLOCK = 50;

  /**
   * {@inheritDoc}
   */
  public DuplicationDebtCalculator(Settings settings) {
    super(settings);
  }

  /**
   * {@inheritDoc}
   */
  public double calculateAbsoluteDebt(DecoratorContext context) {
    Measure measure = context.getMeasure(CoreMetrics.DUPLICATED_BLOCKS);
    if (!MeasureUtils.hasValue(measure)) {
      return 0.0;
    }
    // technicaldebt is calculate in man days
    // FIXME Why no settings.getDouble() ?
    return measure.getValue() * Double.valueOf(settings.getString(TechnicalDebtPlugin.COST_DUPLICATED_BLOCKS)) / HOURS_PER_DAY;
  }

  /**
   * {@inheritDoc}
   */
  public double calculateTotalPossibleDebt(DecoratorContext context) {
    double duplicationDensity = getValue(context, CoreMetrics.DUPLICATED_LINES_DENSITY);
    double absoluteDebt = calculateAbsoluteDebt(context);

    if (duplicationDensity == 0 && absoluteDebt == 0) {
      return 0;
    } else if (duplicationDensity == 0 || absoluteDebt == 0) {
      // FIXME Why no settings.getDouble() ?
      return getValue(context, CoreMetrics.LINES) / NUMBER_OF_LINES_PER_BLOCK * Double.valueOf(settings.getString(TechnicalDebtPlugin.COST_DUPLICATED_BLOCKS)) / HOURS_PER_DAY;
    }

    return absoluteDebt * 100 / duplicationDensity;
  }

  private double getValue(DecoratorContext context, Metric metric) {
    Measure measure = context.getMeasure(metric);
    if (!MeasureUtils.hasValue(measure) || measure.getValue() == 0) {
      return 0.0;
    }
    return measure.getValue();
  }

  /**
   * {@inheritDoc}
   */
  public List<Metric> dependsOn() {
    return Arrays.asList(CoreMetrics.DUPLICATED_BLOCKS, CoreMetrics.DUPLICATED_LINES_DENSITY, CoreMetrics.LINES);
  }

  /**
   * {@inheritDoc}
   */
  public String getName() {
    return "Duplication";

  }
}
