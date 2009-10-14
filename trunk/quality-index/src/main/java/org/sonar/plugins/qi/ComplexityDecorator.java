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
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.KeyValueFormat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * An implementation of AbstractDecorator to measure the QI complexity axis
 */
public class ComplexityDecorator extends AbstractDecorator {

  /**
   * Creates a ComplexityDecorator
   *
   * @param configuration the config
   */
  public ComplexityDecorator(Configuration configuration) {
    super(configuration, QIMetrics.QI_COMPLEXITY,
        QIPlugin.QI_COMPLEXITY_AXIS_WEIGHT, QIPlugin.QI_COMPLEXITY_AXIS_WEIGHT_DEFAULT);
  }

  /**
   * The decorator will generate 3 metrics
   *
   * @return the 3 metrics on complexity
   */
  @DependedUpon
  @Override
  public List<Metric> dependedUpon() {
    return Arrays.asList(QIMetrics.QI_COMPLEXITY_FACTOR, QIMetrics.QI_COMPLEXITY_FACTOR_METHODS, QIMetrics.QI_COMPLEXITY);
  }

  /**
   * The Complexity distribution must be computed before we can start decorating...
   *
   * @return the list of dependencies
   */
  @DependsUpon
  @Override
  public List<Metric> dependsUpon() {
    return Arrays.asList(QIMetrics.QI_COMPLEX_DISTRIBUTION);
  }

  /**
   * Decorates the resource with the 3 indicators, i.e. save the measures
   *
   * @param resource the resource
   * @param context  the context
   */
  public void decorate(Resource resource, DecoratorContext context) {
    // Do not want to decorate anything on unit tests files
    if (Utils.shouldSaveMeasure(resource)) {
      saveSpecificMeasure(context, computeComplexityFactor(context), QIMetrics.QI_COMPLEXITY_FACTOR);
      saveSpecificMeasure(context, computeComplexMethodsCount(context), QIMetrics.QI_COMPLEXITY_FACTOR_METHODS);
      saveMeasure(context, computeComplexity(context));
    }

  }

  /**
   * Compute the complexity axis for the QI
   *
   * @param context the context
   * @return the complexity value
   */
  protected double computeComplexity(DecoratorContext context) {
    double complexity = getMethodCount(context, true);
    return complexity / getValidLines(context);
  }

  /**
   * Computes the complexity factor, i.e. a density of complex methods
   *
   * @param context the context
   * @return the factor
   */
  protected double computeComplexityFactor(DecoratorContext context) {
    double methodWithComplexityCount = getMethodCount(context, false);
    if (methodWithComplexityCount == 0) {
      return 0;
    }
    double complexityFactor = 5 * 100 * getComplexMethods(context) / methodWithComplexityCount;
    return Math.min(complexityFactor, 100);
  }

  /**
   * @param context the context
   * @return the number of complex methods
   */
  protected double computeComplexMethodsCount(DecoratorContext context) {
    return getComplexMethods(context);
  }

  /**
   * Counts the number of methods that are taken into account for complexity indicator calculation
   *
   * @param context  the context
   * @param weighted whether to weight the methods depending on the complexity
   * @return the weighted or not sum
   */
  private double getMethodCount(DecoratorContext context, boolean weighted) {
    Measure measure = context.getMeasure(QIMetrics.QI_COMPLEX_DISTRIBUTION);
    if (measure == null) {
      return 0;
    }
    Map<Integer, Integer> distribution = KeyValueFormat.parse(measure.getData(), new KeyValueFormat.IntegerNumbersPairTransformer());
    double methodWithComplexityCount;
    if (weighted) {
      methodWithComplexityCount = distribution.get(2) + 3 * distribution.get(10) + 5 * distribution.get(20) + 10 * distribution.get(30);
    } else {
      methodWithComplexityCount = distribution.get(2) + distribution.get(10) + distribution.get(20) + distribution.get(30);
    }
    return methodWithComplexityCount;
  }

  /**
   * @param context the context
   * @return the number of methods with a complexity higher than 30
   */
  private double getComplexMethods(DecoratorContext context) {
    Measure measure = context.getMeasure(QIMetrics.QI_COMPLEX_DISTRIBUTION);
    if (measure == null) {
      return 0;
    }
    Map<Integer, Integer> distribution = KeyValueFormat.parse(measure.getData(), new KeyValueFormat.IntegerNumbersPairTransformer());
    return distribution.get(30);
  }

  /**
   * Method used to save extra measures : complexity factor and number of complex methods
   *
   * @param context the context
   * @param value   the value
   * @param metric  the metric
   */
  private void saveSpecificMeasure(DecoratorContext context, double value, Metric metric) {
    context.saveMeasure(metric, value);
  }
}
