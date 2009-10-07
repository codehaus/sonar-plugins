package org.sonar.plugins.qi;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.NavigationSection;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;

@NavigationSection(NavigationSection.RESOURCE)
@UserRole(UserRole.VIEWER)
public class QIWidget extends AbstractRubyTemplate implements RubyRailsWidget {

  public String getId() {
    return "quality-index";
  }

  public String getTitle() {
    // not used for the moment by widgets.
    return "Quality Index";
  }

  @Override
  protected String getTemplatePath() {
    return "c:\\toto.erb";
  }
}