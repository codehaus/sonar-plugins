package org.sonar.plugins.qi;

import org.sonar.api.CoreProperties;
import org.sonar.api.measures.Metric;
import org.apache.commons.configuration.Configuration;

public class CodingViolationsDecorator extends AbstractViolationsDecorator {

  public CodingViolationsDecorator(Configuration configuration) {
    super(configuration, QIMetrics.QI_CODING_VIOLATIONS,
      QIPlugin.QI_CODING_AXIS_WEIGHT, QIPlugin.QI_CODING_AXIS_WEIGHT_DEFAULT);
  }

  @Override
  public String getConfigurationKey() {
    return QIPlugin.QI_CODING_PRIORITY_WEIGHTS;
  }

  @Override
  public String getDefaultConfigurationKey() {
    return QIPlugin.QI_CODING_PRIORITY_WEIGHTS_DEFAULT;
  }

  public Metric getWeightedViolationMetricKey() {
    return QIMetrics.QI_CODING_WEIGHTED_VIOLATIONS;
  }

  @Override
  public String getPluginKey() {
    return CoreProperties.PMD_PLUGIN;
  }
}
