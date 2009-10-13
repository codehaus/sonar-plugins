package org.sonar.plugins.qi;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.apache.commons.configuration.Configuration;

import java.util.List;
import java.util.Arrays;

import com.google.common.collect.Lists;

/**
 * An abstract class from which all decorators that decorate axes of the QI should extend  
 */
public abstract class AbstractDecorator implements Decorator {
  private Metric metric;
  private String axisWeight;
  private String defaultAxisWeight;
  protected Configuration configuration;

  public AbstractDecorator(Configuration configuration, Metric metric, String axisWeight, String defaultAxisWeight) {
    this.metric = metric;
    this.axisWeight = axisWeight;
    this.defaultAxisWeight = defaultAxisWeight;
    this.configuration = configuration;
  }

  @DependedUpon
  public List<Metric> dependedUpon() {
    return Arrays.asList(metric);
  }

  @DependsUpon
  public List<Metric> aggregDependsUpon() {
    List<Metric> list = Lists.newArrayList(CoreMetrics.DUPLICATED_LINES, CoreMetrics.NCLOC);
    list.addAll(dependsUpon());

    return list;
  }

  public abstract List<Metric> dependsUpon();

  protected double getValidLines(DecoratorContext context) {
    double duplicatedLines = MeasureUtils.getValue(context.getMeasure(CoreMetrics.DUPLICATED_LINES), 0.0);
    double totalLines = MeasureUtils.getValue(context.getMeasure(CoreMetrics.NCLOC), 0.0);
    double validLines = totalLines - duplicatedLines;

    return validLines > 0 ? validLines : 1.0;
  }

  protected void saveMeasure(DecoratorContext context, double value) {
    if (value <= 0) {
      return;
    }
    context.saveMeasure(metric, value > 1 ? 1 : value * computeAxisWeight());
  }

  private double computeAxisWeight() {
    return configuration.getDouble(axisWeight, Double.valueOf(defaultAxisWeight));
  }

  public boolean shouldExecuteOnProject(Project project) {
    return QIPlugin.shouldExecuteOnProject(project);
  }

}
