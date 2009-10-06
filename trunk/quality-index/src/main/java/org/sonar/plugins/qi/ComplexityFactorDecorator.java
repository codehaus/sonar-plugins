package org.sonar.plugins.qi;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Project;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Measure;
import org.sonar.api.utils.KeyValueFormat;

import java.util.List;
import java.util.Arrays;
import java.util.Map;

public class ComplexityFactorDecorator implements Decorator{
  public boolean shouldExecuteOnProject(Project project) {
    return QIPlugin.shouldExecuteOnProject(project);
  }

  @DependedUpon
  public List<Metric> dependedUpon() {
    return Arrays.asList(QIMetrics.QI_COMPLEXITY_FACTOR, QIMetrics.QI_COMPLEXITY_FACTOR_METHODS);
  }

  @DependsUpon
  public List<Metric> dependsUpon() {
    return Arrays.asList(QIMetrics.QI_COMPLEX_DISTRIBUTION);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    double complexityFactor = computeComplexityFactor(context);
    if (complexityFactor != 0) {
      context.saveMeasure(QIMetrics.QI_COMPLEXITY_FACTOR, complexityFactor);
    }
  }

  protected double computeComplexityFactor(DecoratorContext context) {
    Measure measure = context.getMeasure(QIMetrics.QI_COMPLEX_DISTRIBUTION);
    if(measure == null) {
      return 0;
    }
    Map<Integer,Integer> distribution = KeyValueFormat.parse(measure.getData(), new KeyValueFormat.IntegerNumbersPairTransformer());
    double complexityFactor = 5 * distribution.get(30) * 100;
    complexityFactor /= distribution.get(1) + distribution.get(10) +distribution.get(20) +distribution.get(30); 

    return complexityFactor > 100 ? 100 : complexityFactor;
  }
}
