package org.sonar.plugins.qi;

import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.resources.Resource;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Measure;
import org.sonar.api.utils.KeyValueFormat;
import org.apache.commons.configuration.Configuration;

import java.util.List;
import java.util.Arrays;
import java.util.Map;

public class ComplexityDecorator extends AbstractDecorator {

  public ComplexityDecorator(Configuration configuration) {
    super(configuration, QIMetrics.QI_COMPLEXITY,
      QIPlugin.QI_COMPLEXITY_AXIS_WEIGHT, QIPlugin.QI_COMPLEXITY_AXIS_WEIGHT_DEFAULT);
  }

  @DependedUpon @Override
  public List<Metric> dependedUpon() {
    return Arrays.asList(QIMetrics.QI_COMPLEXITY_FACTOR, QIMetrics.QI_COMPLEXITY_FACTOR_METHODS, QIMetrics.QI_COMPLEXITY);
  }

  @DependsUpon @Override
  public List<Metric> dependsUpon() {
    return Arrays.asList(QIMetrics.QI_COMPLEX_DISTRIBUTION);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    saveSpecificMeasure(context, computeComplexityFactor(context), QIMetrics.QI_COMPLEXITY_FACTOR);
    saveSpecificMeasure(context, computeComplexMethodsCount(context), QIMetrics.QI_COMPLEXITY_FACTOR_METHODS);
    saveMeasure(context, computeComplexity(context));
  }

  protected double computeComplexity(DecoratorContext context) {
    double complexity = getMethodCount(context, true);
    return complexity / getValidLines(context);
  }

  protected double computeComplexityFactor(DecoratorContext context) {
    double methodWithComplexityCount = getMethodCount(context, false);
    if (methodWithComplexityCount == 0) {
      return 0;
    }
    double complexityFactor = 5 * 100 * getComplexMethods(context) / methodWithComplexityCount;

    return complexityFactor > 100 ? 100 : complexityFactor;
  }

  protected double computeComplexMethodsCount(DecoratorContext context) {
    return getComplexMethods(context);
  }

  private double getMethodCount(DecoratorContext context, boolean weighted) {
    Measure measure = context.getMeasure(QIMetrics.QI_COMPLEX_DISTRIBUTION);
    if (measure == null) {
      return 0;
    }
    Map<Integer, Integer> distribution = KeyValueFormat.parse(measure.getData(), new KeyValueFormat.IntegerNumbersPairTransformer());
    double methodWithComplexityCount;
    if (weighted) {
      methodWithComplexityCount = distribution.get(2) + 3 * distribution.get(10) + 5 * distribution.get(20) + 10 * distribution.get(30);
    }
    else {
      methodWithComplexityCount = distribution.get(2) + distribution.get(10) + distribution.get(20) + distribution.get(30);
    }
    return methodWithComplexityCount;
  }

  private double getComplexMethods(DecoratorContext context) {
    Measure measure = context.getMeasure(QIMetrics.QI_COMPLEX_DISTRIBUTION);
    if (measure == null) {
      return 0;
    }
    Map<Integer, Integer> distribution = KeyValueFormat.parse(measure.getData(), new KeyValueFormat.IntegerNumbersPairTransformer());
    return distribution.get(30);
  }

  private void saveSpecificMeasure(DecoratorContext context, double value, Metric metric) {
    if (value > 0) {
      context.saveMeasure(metric, value);
    }
  }
}
