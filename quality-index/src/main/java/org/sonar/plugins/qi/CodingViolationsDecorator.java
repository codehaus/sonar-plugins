package org.sonar.plugins.qi;

import org.sonar.api.measures.Metric;
import org.sonar.api.CoreProperties;
import org.apache.commons.configuration.Configuration;

public class CodingViolationsDecorator extends AbstractViolationsDecorator {

  public CodingViolationsDecorator(Configuration configuration) {
    super(configuration);
  }

  @Override
  public Metric getGeneratedMetrics() {
    return QIMetrics.QI_CODING_VIOLATIONS;
  }

  @Override
  public String getConfigurationKey() {
    return QIPlugin.QI_CODING_PRIORITY_WEIGHTS;
  }

  @Override
  public String getDefaultConfigurationKey() {
    return QIPlugin.QI_CODING_PRIORITY_WEIGHTS_DEFAULT;
  }

  @Override
  public String getPluginKey() {
    return CoreProperties.PMD_PLUGIN;
  }
}
