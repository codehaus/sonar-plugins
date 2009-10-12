package org.sonar.plugins.qi;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.CoreMetrics;

public abstract class AbstractDecorator implements Decorator {

  protected double getValidLines(DecoratorContext context) {
    double duplicatedLines = MeasureUtils.getValue(context.getMeasure(CoreMetrics.DUPLICATED_LINES), 0.0);
    double totalLines = MeasureUtils.getValue(context.getMeasure(CoreMetrics.NCLOC), 0.0);
    double validLines = totalLines - duplicatedLines;

    return validLines > 0 ? validLines : 0.0;
  }
}
