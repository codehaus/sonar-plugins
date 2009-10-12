package org.sonar.plugins.qi;

import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Project;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.MeasureUtils;

import java.util.List;
import java.util.Arrays;

public class CoverageDecorator extends AbstractDecorator{
  public boolean shouldExecuteOnProject(Project project) {
    return QIPlugin.shouldExecuteOnProject(project);
  }

  @DependedUpon
  public List<Metric> dependedUpon() {
    return Arrays.asList(QIMetrics.QI_TEST_COVERAGE);
  }

  @DependsUpon
  public List<Metric> dependsUpon() {
    return Arrays.asList(CoreMetrics.LINE_COVERAGE);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    saveMeasure(context, computeCoverageFactor(context), QIMetrics.QI_TEST_COVERAGE);
  }

  private double computeCoverageFactor(DecoratorContext context) {
    double coveredLines = MeasureUtils.getValue(context.getMeasure(CoreMetrics.LINE_COVERAGE), 0.0);
    return 1 - coveredLines / getValidLines(context);

  }
}
