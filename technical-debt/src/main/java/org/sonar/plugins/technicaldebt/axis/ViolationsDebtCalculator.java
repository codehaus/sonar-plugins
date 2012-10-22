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
public final class ViolationsDebtCalculator extends AxisDebtCalculator {
  public ViolationsDebtCalculator(Settings settings) {
    super(settings);
  }

  /**
   * {@inheritDoc}
   */
  public double calculateAbsoluteDebt(DecoratorContext context) {
    // SONAR-996 would enable to be much sharper in the evaluation of resolution time

    // The plugin does not take violations in info priority into account
    Measure mViolations = context.getMeasure(CoreMetrics.VIOLATIONS);
    Measure mInfoViolations = context.getMeasure(CoreMetrics.INFO_VIOLATIONS);

    double violations = (MeasureUtils.hasValue(mViolations) ? mViolations.getValue() : 0.0)
      - (MeasureUtils.hasValue(mInfoViolations) ? mInfoViolations.getValue() : 0.0);

    // technicaldebt is calculate in man days
    // FIXME Why no settings.getDouble() ?
    return violations * Double.valueOf(settings.getString(TechnicalDebtPlugin.COST_VIOLATION)) / HOURS_PER_DAY;
  }

  /**
   * {@inheritDoc}
   */
  public double calculateTotalPossibleDebt(DecoratorContext context) {
    // First we calculate the number of violations in the system necessary to have a RCI of zero
    double loc = getValue(context, CoreMetrics.NCLOC);
    double weightedViolations = getValue(context, CoreMetrics.WEIGHTED_VIOLATIONS);
    double mViolations = getValue(context, CoreMetrics.VIOLATIONS);
    double mInfoViolations = getValue(context, CoreMetrics.INFO_VIOLATIONS);

    double violations = mViolations - mInfoViolations;

    // we have a serious problem somewhere !
    if (loc == 0) {
      return 0;
    }

    if (violations == 0 || weightedViolations == 0) {
      // In that case, we say that a major violation every 3 lines of code in average means a RCI of 0%
      // FIXME Why no settings.getDouble() ?
      return loc / 3 * Double.valueOf(settings.getString(TechnicalDebtPlugin.COST_VIOLATION)) / HOURS_PER_DAY;
    }

    // Otherwise we calculate based on existing violations
    // FIXME Why no settings.getDouble() ?
    return loc / weightedViolations * violations * Double.valueOf(settings.getString(TechnicalDebtPlugin.COST_VIOLATION)) / HOURS_PER_DAY;
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
    return Arrays.asList(CoreMetrics.VIOLATIONS, CoreMetrics.INFO_VIOLATIONS, CoreMetrics.NCLOC, CoreMetrics.WEIGHTED_VIOLATIONS);
  }

  /**
   * {@inheritDoc}
   */
  public String getName() {
    return "Violations";
  }
}
