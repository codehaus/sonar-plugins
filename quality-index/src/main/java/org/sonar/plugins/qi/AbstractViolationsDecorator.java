package org.sonar.plugins.qi;

import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.utils.KeyValueFormat;
import org.sonar.api.utils.KeyValue;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.List;

import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;

public abstract class AbstractViolationsDecorator extends AbstractDecorator {

  public AbstractViolationsDecorator(Configuration configuration, Metric metric,
                                     String axisWeight, String defaultAxisWeight) {
    super(configuration, metric, axisWeight, defaultAxisWeight);
  }

  public abstract String getConfigurationKey();

  public abstract String getDefaultConfigurationKey();

  public abstract String getPluginKey();

  @Override
  public List<Metric> dependsUpon() {
    return Lists.newArrayList(CoreMetrics.VIOLATIONS);
  }

  protected double getRate(DecoratorContext context) {
    double rate;

    Multiset<RulePriority> violations = countViolationsByPriority(context);
    Map<RulePriority, Integer> weights = getWeightsByPriority();

    double weightedViolations = getWeightedViolations(weights, violations);
    rate = weightedViolations / getValidLines(context);

    if (rate >= 1.0) {
      return 1.0;
    }
    return rate;
  }

  public boolean shouldExecuteOnProject(Project project) {
    return QIPlugin.shouldExecuteOnProject(project);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    saveMeasure(context, getRate(context));
  }

  protected Multiset<RulePriority> countViolationsByPriority(DecoratorContext context) {
    List<Violation> violations = context.getViolations();
    Multiset<RulePriority> violationsByPriority = HashMultiset.create();

    for (Violation violation : violations) {
      if (violation.getRule().getPluginName().equals(getPluginKey())) {
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

  protected Map<RulePriority, Integer> getWeightsByPriority() {
    String property = configuration.getString(getConfigurationKey(), getDefaultConfigurationKey());

    return KeyValueFormat.parse(property, new RulePriorityNumbersPairTransformer());
  }


  // This can be removed as soon as SOnar 1.12 is out
  public static class RulePriorityNumbersPairTransformer implements KeyValueFormat.Transformer<RulePriority, Integer> {

    public KeyValue<RulePriority, Integer> transform(String key, String value) {
      try {
        if (StringUtils.isBlank(value)) { value = "0"; }
        return new KeyValue<RulePriority, Integer>(RulePriority.valueOf(key.toUpperCase()), Integer.parseInt(value));
      }
      catch (Exception e) {
        LoggerFactory.getLogger(RulePriorityNumbersPairTransformer.class).warn("Property " + key + " has invalid value: " + value, e);
        return null;
      }
    }
  }
}
