package org.sonar.plugins.qi;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.utils.KeyValueFormat;
import org.apache.commons.configuration.Configuration;

import java.util.Map;
import java.util.List;

import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;

public abstract class AbstractDecorator implements Decorator {
  private Configuration configuration;

  public AbstractDecorator(Configuration configuration) {
    this.configuration = configuration;
  }

  @DependsUpon
  public abstract List<Metric> getRequiredMetrics();

  @DependedUpon
  public abstract List<Metric> getGeneratedMetrics();

  protected abstract double getRate(DecoratorContext context);

  public boolean shouldExecuteOnProject(Project project) {
    return QIPlugin.shouldExecuteOnProject(project);
  }

  public void decorate(Resource resource, DecoratorContext context) {
  }

  protected Multiset<RulePriority> countViolationsByPriority(DecoratorContext context, String pluginName) {
    List<Violation> violations = context.getViolations();
    Multiset<RulePriority> violationsByPriority = HashMultiset.create();

    for (Violation violation : violations) {
      if (violation.getRule().getPluginName().equals(pluginName)) {
        violationsByPriority.add(violation.getPriority());
      }
    }
    return violationsByPriority;
  }


  protected double getWeightedViolations(Map<RulePriority, Integer> weights, Multiset<RulePriority> violations) {
    double weightedViolations = 0.0;

    for (RulePriority priority : weights.keySet()) {
      weightedViolations += weights.get(priority) * violations.count(priority);
    }
    return weightedViolations;
  }

  protected double getValidLines(DecoratorContext context) {
    double duplicatedLines = MeasureUtils.getValue(context.getMeasure(CoreMetrics.DUPLICATED_LINES), 0.0);
    double totalLines = MeasureUtils.getValue(context.getMeasure(CoreMetrics.NCLOC), 0.0);
    double validLines = totalLines - duplicatedLines;

    return validLines > 0 ? validLines : 0.0;
  }

  protected Map<RulePriority, Integer> getWeightsByPriority(String key, String defaultKey) {
    String property = configuration.getString(key, defaultKey);
    return KeyValueFormat.parse(property, new KeyValueFormat.RulePriorityNumbersPairTransformer());
  }
}
