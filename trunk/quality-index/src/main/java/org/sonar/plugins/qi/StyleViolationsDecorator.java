

package org.sonar.plugins.qi;

import org.sonar.api.measures.Metric;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.DecoratorContext;
import org.apache.commons.configuration.Configuration;

public class StyleViolationsDecorator extends AbstractViolationsDecorator {

  public StyleViolationsDecorator(Configuration configuration) {
    super(configuration);
  }

  @Override
  public double getValidLines(DecoratorContext context) {
    return super.getValidLines(context) * 10;
  }

  @Override
  public Metric getGeneratedMetrics() {
    return QIMetrics.QI_STYLE_VIOLATIONS;
  }

  @Override
  public String getConfigurationKey() {
    return QIPlugin.QI_STYLE_PRIORITY_WEIGHTS;
  }

  @Override
  public String getDefaultConfigurationKey() {
    return QIPlugin.QI_STYLE_PRIORITY_WEIGHTS_DEFAULT;
  }

  @Override
  public String getPluginKey() {
    return CoreProperties.CHECKSTYLE_PLUGIN;
  }
}
