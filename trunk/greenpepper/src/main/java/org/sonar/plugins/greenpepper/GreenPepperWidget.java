package org.sonar.plugins.greenpepper;

import org.sonar.api.web.AbstractDashboardWidget;

public class GreenPepperWidget extends AbstractDashboardWidget {

  @Override
  protected String getTemplatePath() {
    return "/org/sonar/plugins/greenpepper/greenpepperWidget.erb";
  }

}
