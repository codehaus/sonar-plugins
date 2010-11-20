/*
 * Sonar Basic-COCOMO Plugin
 * Copyright (C) 2010 Xup BV.
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.cocomo;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

/**
 * {@inheritDoc}
 */
public class BasicCOCOMODecorator implements Decorator {

  private final Configuration configuration;

  /**
   * {@inheritDoc}
   */
  public BasicCOCOMODecorator(Configuration configuration) {
    this.configuration = configuration;
  }

  @DependsUpon
  public final List<Metric> dependsOnMetrics() {
    return Arrays.asList(
      CoreMetrics.NCLOC);
  }

  @DependedUpon
  public final List<Metric> generatesMetrics() {
    return Arrays.asList(
      BasicCOCOMOMetrics.COCOMO_COST,
      BasicCOCOMOMetrics.COCOMO_EFFORT_APPLIED,
      BasicCOCOMOMetrics.COCOMO_DEV_TIME,
      BasicCOCOMOMetrics.COCOMO_PEOPLE_REQ);
  }

  /**
   * {@inheritDoc}
   */
  public final boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  /**
   * http://en.wikipedia.org/wiki/COCOMO
   * {@inheritDoc}
   */
  public final void decorate(Resource resource, DecoratorContext decoratorContext) {
    double effortApplied = calculateAppliedEffort( decoratorContext );
    double developmentTime = calculateDevelopmentTime( effortApplied );
    double peopleRequired = effortApplied / developmentTime;
    double estimatedCost = calculateCost( effortApplied );

    saveMeasure(decoratorContext, BasicCOCOMOMetrics.COCOMO_EFFORT_APPLIED, effortApplied);
    saveMeasure(decoratorContext, BasicCOCOMOMetrics.COCOMO_DEV_TIME, developmentTime);
    saveMeasure(decoratorContext, BasicCOCOMOMetrics.COCOMO_PEOPLE_REQ, peopleRequired);
    saveMeasure(decoratorContext, BasicCOCOMOMetrics.COCOMO_COST, estimatedCost);
  }

  // Calculates the basic COCOMO Effort Applied measure Ab (KLOC)^Bb 
  private double calculateAppliedEffort( DecoratorContext decoratorContext ) {
    Measure mNLOC = decoratorContext.getMeasure(CoreMetrics.NCLOC);

    // Determine the total number of lines (non commented lines + commented lines)
    double totalLines = (MeasureUtils.hasValue(mNLOC) ? mNLOC.getValue() : 0.0);
    double kloc = totalLines / 1000;

    // Effort applied is calculated in man months
    double ab = getWeight(BasicCOCOMOPlugin.CCM_COEFFICIENT_AB, BasicCOCOMOPlugin.CCM_COEFFICIENT_AB_DEFAULT);
    double bb = getWeight(BasicCOCOMOPlugin.CCM_COEFFICIENT_BB, BasicCOCOMOPlugin.CCM_COEFFICIENT_BB_DEFAULT);
    return ab * Math.pow(kloc, bb);
  }

  // Calculates the basic COCOMO development time  Cb (effortApplied)^Db 
  private double calculateDevelopmentTime( double effortApplied ) {
    // Development time is calculated in months
    double cb = getWeight(BasicCOCOMOPlugin.CCM_COEFFICIENT_CB, BasicCOCOMOPlugin.CCM_COEFFICIENT_CB_DEFAULT);
    double db = getWeight(BasicCOCOMOPlugin.CCM_COEFFICIENT_DB, BasicCOCOMOPlugin.CCM_COEFFICIENT_DB_DEFAULT);
    return cb * Math.pow(effortApplied, db);
  }

  // Calculates the basic COCOMO development cost (development time  monthlyRate) 
  private double calculateCost( double developmentTime ) {
    // Estimated project codes
    double monthlyRate = getWeight(BasicCOCOMOPlugin.CCM_MONTHLY_RATE, BasicCOCOMOPlugin.CCM_MONTHLY_RATE_DEFAULT);
    return developmentTime * monthlyRate;
  }

  private double getWeight(String keyWeight, String defaultWeight) {
    Object property = configuration.getProperty(keyWeight);
    if (property != null) {
      return Double.parseDouble((String) property);
    }
    return Double.parseDouble(defaultWeight);
  }

  private void saveMeasure(DecoratorContext decoratorContext, Metric metric, double measure) {
    if (measure * 10 > 1) {
      decoratorContext.saveMeasure(metric, measure);
    }
  }
}
