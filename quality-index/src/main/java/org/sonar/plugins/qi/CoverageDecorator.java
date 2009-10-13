package org.sonar.plugins.qi;

import org.sonar.api.resources.Resource;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.MeasureUtils;
import org.apache.commons.configuration.Configuration;

import java.util.List;
import java.util.Arrays;

public class CoverageDecorator extends AbstractDecorator{

  public CoverageDecorator(Configuration configuration) {
    super(configuration, QIMetrics.QI_TEST_COVERAGE,
      QIPlugin.QI_COVERAGE_AXIS_WEIGHT, QIPlugin.QI_COVERAGE_AXIS_WEIGHT_DEFAULT);
  }

  @Override
  public List<Metric> dependsUpon() {
    return Arrays.asList(CoreMetrics.LINES_TO_COVER, CoreMetrics.UNCOVERED_LINES);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    saveMeasure(context, computeCoverageFactor(context));
  }

  private double computeCoverageFactor(DecoratorContext context) {
    double linesToCover = MeasureUtils.getValue(context.getMeasure(CoreMetrics.LINES_TO_COVER), 0.0);
    double uncoveredLines = MeasureUtils.getValue(context.getMeasure(CoreMetrics.UNCOVERED_LINES), 0.0);

    double coveredLines = linesToCover - uncoveredLines;
    return 1 - (coveredLines / getValidLines(context));
  }
}
