package org.sonar.plugins.qi;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Java;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.utils.KeyValueFormat;
import org.sonar.api.utils.KeyValue;
import org.apache.commons.configuration.Configuration;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QualityIndexDecorator implements Decorator {
  private Configuration configuration;

  public QualityIndexDecorator(Configuration configuration) {
    this.configuration = configuration;
  }

  public boolean shouldExecuteOnProject(Project project) {
    return project.getLanguage().equals(Java.INSTANCE);
  }

  public void decorate(Resource resource, DecoratorContext context) {

  }

  protected double getCodingRate(DecoratorContext context) {
    double rate = 0.0;

/*    String key = QualityIndexPlugin.QI_CODING_PRIORITY_WEIGHTS;
    String defaultKey = QualityIndexPlugin.QI_CODING_PRIORITY_WEIGHTS_DEFAULT;

     Map<RulePriority, Integer> weights = getWeights(key, defaultKey);



    rate = getWeightedViolations(weights, getEmptyViolationsMap(Lists.newArrayList(weights.keySet()))) / getValidLines(context);
  */
    if (rate >= 1.0) {
      return 1.0;
    }
    return rate;
  }



  protected double getWeightedViolations(Map<RulePriority, Integer> weights, Map<RulePriority, Integer> violations) {
    double weightedViolations = 0.0;

    for (RulePriority priority : weights.keySet()) {
      weightedViolations += weights.get(priority) * violations.get(priority);
    }
    return weightedViolations;
  }

  protected HashMap<RulePriority, Integer> getEmptyViolationsMap(List<RulePriority> keys) {
    HashMap<RulePriority, Integer> violations = new HashMap<RulePriority, Integer>();
    for (RulePriority priority : keys) {
      violations.put(priority, 0);
    }
    return violations;
  }

  protected double getValidLines(DecoratorContext context) {
    double duplicatedLines = MeasureUtils.getValue(context.getMeasure(CoreMetrics.DUPLICATED_LINES), 0.0);
    double totalLines = MeasureUtils.getValue(context.getMeasure(CoreMetrics.NCLOC), 0.0);
    double validLines = totalLines - duplicatedLines;

    return validLines > 0 ? validLines : 0.0;
  }

  protected Map<RulePriority, Integer> getWeights(String key, String defaultKey) {
    String property = configuration.getString(key, defaultKey);

    Map<RulePriority, Integer> weights = KeyValueFormat.parse(property, new KeyValueFormat.Transformer<RulePriority, Integer>() {
      public KeyValue<RulePriority, Integer> transform(String key, String value) {
        try {
          return new KeyValue<RulePriority, Integer>(RulePriority.valueOf(key.toUpperCase()), Integer.parseInt(value));
        } catch (Exception e) {
          LoggerFactory.getLogger(QualityIndexDecorator.class).warn("Property " + key + " has invalid value: " + value, e);
          return null;
        }
      }
    });
    return weights;
  }
}
