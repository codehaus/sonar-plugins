

package org.sonar.plugins.qi;

import org.sonar.api.CoreProperties;
import org.sonar.api.batch.DecoratorContext;
import org.apache.commons.configuration.Configuration;

public class StyleViolationsDecorator extends AbstractViolationsDecorator {

  public StyleViolationsDecorator(Configuration configuration) {
    super(configuration, QIMetrics.QI_STYLE_VIOLATIONS,
      QIPlugin.QI_STYLE_AXIS_WEIGHT, QIPlugin.QI_STYLE_AXIS_WEIGHT_DEFAULT);
  }

  @Override
  public double getValidLines(DecoratorContext context) {
    return super.getValidLines(context) * 10;
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
