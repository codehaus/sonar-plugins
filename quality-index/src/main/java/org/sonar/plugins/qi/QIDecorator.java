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

/**
 * The decorator implementation  to calculate the Quality Index
 */
public class QIDecorator implements Decorator{

  /**
   * All 4 axes should be calculated before QI computation
   *
   * @return the 4 metrics
   */
  @DependsUpon
  public List<Metric> getRequiredMetrics() {
    return Arrays.asList(QIMetrics.QI_COMPLEXITY, QIMetrics.QI_TEST_COVERAGE,
      QIMetrics.QI_CODING_VIOLATIONS, QIMetrics.QI_STYLE_VIOLATIONS);
  }

  /**
   * @return the QI metric
   */
  @DependedUpon
  public List<Metric> getGeneratedMetrics() {
    return Arrays.asList(QIMetrics.QI_QUALITY_INDEX);
  }

  /**
   * @param project the project
   * @return whether to execute the decorator on the project
   */
  public boolean shouldExecuteOnProject(Project project) {
    return QIPlugin.shouldExecuteOnProject(project);
  }

  /**
   * The decorate action
   *
   * @param resource the resource
   * @param context the context
   */
  public void decorate(Resource resource, DecoratorContext context) {
    double value = 10;
    for (Metric metric : getRequiredMetrics()) {
      value -= MeasureUtils.getValue(context.getMeasure(metric), 0.0);
    }
    context.saveMeasure(QIMetrics.QI_QUALITY_INDEX, value);
  }
}
