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

public class QICodingDecorator extends QIAbstractDecorator {

  public QICodingDecorator(Configuration configuration) {
    super(configuration);
  }

  public List<Metric> getRequiredMetrics() {
    return Arrays.asList(CoreMetrics.NCLOC, CoreMetrics.DUPLICATED_LINES);
  }

  public List<Metric> getGeneratedMetrics() {
    return Arrays.asList(QualityIndexMetrics.QI_CODING_VIOLATIONS);
  }

  protected double getRate(DecoratorContext context) {
    double rate;
    String key = QualityIndexPlugin.QI_CODING_PRIORITY_WEIGHTS;
    String defaultKey = QualityIndexPlugin.QI_CODING_PRIORITY_WEIGHTS_DEFAULT;

    Multiset<RulePriority> violations = countViolationsByPriority(context, CoreProperties.PMD_PLUGIN);
    Map<RulePriority, Integer>weights = getWeightsByPriority(key, defaultKey);

    double weightedViolations = getWeightedViolations(weights, violations);
    rate = weightedViolations / getValidLines(context);

    if (rate >= 1.0) {
      return 1.0;
    }
    return rate;
  }
}
