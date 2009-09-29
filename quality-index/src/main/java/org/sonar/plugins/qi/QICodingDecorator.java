package org.sonar.plugins.qi;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Project;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.KeyValueFormat;
import org.sonar.api.CoreProperties;
import org.apache.commons.configuration.Configuration;
import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class QICodingDecorator implements Decorator {
  private Configuration configuration;

  public QICodingDecorator(Configuration configuration) {
    this.configuration = configuration;
  }

  public boolean shouldExecuteOnProject(Project project) {
    return QualityIndexPlugin.shouldExecuteOnProject(project);
  }

  @DependsUpon
  public List<Metric> getRequiredMetrics() {
    return Arrays.asList(CoreMetrics.NCLOC, CoreMetrics.DUPLICATED_LINES);
  }

  @DependedUpon
  public List<Metric> getGeneratedMetrics() {
    return Arrays.asList(QualityIndexMetrics.QI_CODING_VIOLATIONS);
  }

  public void decorate(Resource resource, DecoratorContext context) {

  }

  protected double getCodingRate(DecoratorContext context) {
    double rate;
    String key = QualityIndexPlugin.QI_CODING_PRIORITY_WEIGHTS;
    String defaultKey = QualityIndexPlugin.QI_CODING_PRIORITY_WEIGHTS_DEFAULT;

    Multiset<RulePriority> violations = countViolationsByPriority(context);
    Map<RulePriority, Integer>weights = getWeightsByPriority(key, defaultKey);

    double weightedViolations = getWeightedViolations(weights, violations);
    rate = weightedViolations / getValidLines(context);

    if (rate >= 1.0) {
      return 1.0;
    }
    return rate;
  }

  protected Multiset<RulePriority> countViolationsByPriority(DecoratorContext context) {
    List<Violation> violations = context.getViolations();
    Multiset<RulePriority> violationsByPriority = HashMultiset.create();

    for (Violation violation : violations) {
      if(violation.getRule().getPluginName().equals(CoreProperties.PMD_PLUGIN)) {
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
