package org.sonar.plugins.qi;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.MeasureUtils;

import java.util.List;
import java.util.Arrays;

public class QIDecorator implements Decorator{
  @DependsUpon
  public List<Metric> getRequireMetrics() {
    return Arrays.asList(QIMetrics.QI_COMPLEXITY, QIMetrics.QI_TEST_COVERAGE,
      QIMetrics.QI_CODING_VIOLATIONS, QIMetrics.QI_STYLE_VIOLATIONS);
  }

  @DependedUpon
  public List<Metric> getGeneratedMetrics() {
    return Arrays.asList(QIMetrics.QI_QUALITY_INDEX);
  }

  public boolean shouldExecuteOnProject(Project project) {
    return QIPlugin.shouldExecuteOnProject(project);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    double value = 10;
    for (Metric metric : getRequireMetrics()) {
      value -= MeasureUtils.getValue(context.getMeasure(metric), 0.0);
    }
    context.saveMeasure(QIMetrics.QI_QUALITY_INDEX, value);
  }
}
