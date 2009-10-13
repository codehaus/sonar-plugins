package org.sonar.plugins.qi;

import org.sonar.api.CoreProperties;
import org.sonar.api.measures.Metric;
import org.apache.commons.configuration.Configuration;

public class CodingViolationsDecorator extends AbstractViolationsDecorator {

  /**
   * Creates a CodingViolationsDecorator, i.e. implements an AbstractViolationsDecorator
   * to decorate the Coding axis of the QI
   *
   * @param configuration the configuration
   */
  public CodingViolationsDecorator(Configuration configuration) {
    super(configuration, QIMetrics.QI_CODING_VIOLATIONS,
      QIPlugin.QI_CODING_AXIS_WEIGHT, QIPlugin.QI_CODING_AXIS_WEIGHT_DEFAULT);
  }

  /**
   * @return the coding axis weight config key
   */
  @Override
  public String getConfigurationKey() {
    return QIPlugin.QI_CODING_PRIORITY_WEIGHTS;
  }

  /**
   * @return the coding axis default weight config key
   */
  @Override
  public String getDefaultConfigurationKey() {
    return QIPlugin.QI_CODING_PRIORITY_WEIGHTS_DEFAULT;
  }

  /**
   * @return the metric to store the coding weighted violations
   */
  public Metric getWeightedViolationMetricKey() {
    return QIMetrics.QI_CODING_WEIGHTED_VIOLATIONS;
  }

  /**
   * @return the PMD key
   */
  @Override
  public String getPluginKey() {
    return CoreProperties.PMD_PLUGIN;
  }
}
