package org.codehaus.sonar.cql;

import org.sonar.api.web.*;

@UserRole(UserRole.USER)
@Description("Show how to use Ruby Widget API")
@WidgetProperties({
        @WidgetProperty(key="paramXdepend1",
                        description="This is a Xdepend mandatory parameter",
                        optional=false
        ),
        @WidgetProperty(key="maxXdepend",
                        description="max Xdepend threshold",
                        type=WidgetPropertyType.INTEGER,
                        defaultValue="80"
        ),
        @WidgetProperty(key="paramXdepend2",
                        description="This is an optional parameter Xdepend "
        ),
        @WidgetProperty(key="floatprop",
                        description="test description Xdepend"
        )
})
public class CqlDashboardWidget extends AbstractRubyTemplate implements RubyRailsWidget {

  public String getId() {
    return "Xdepend";
  }

  public String getTitle() {
    return "Xdepend";
  }

  @Override
  protected String getTemplatePath() {
    return "/xdepend_dashboard_widget.html.erb";
  }
}