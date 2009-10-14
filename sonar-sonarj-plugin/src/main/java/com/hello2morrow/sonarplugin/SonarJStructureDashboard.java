package com.hello2morrow.sonarplugin;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.NavigationSection;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;

@NavigationSection(NavigationSection.RESOURCE)
@UserRole(UserRole.VIEWER)
public class SonarJStructureDashboard extends AbstractRubyTemplate implements RubyRailsWidget
{

    public String getId()
    {
        return "sonarj";
    }

    public String getTitle()
    {
        // not used for the moment by widgets.
        return "SonarJ";
    }

    protected String getTemplatePath()
    {
        return "/sonarj_structure_widget.html.erb";
    }
}