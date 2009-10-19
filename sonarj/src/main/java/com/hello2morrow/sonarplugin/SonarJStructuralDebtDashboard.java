package com.hello2morrow.sonarplugin;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.NavigationSection;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;

@NavigationSection(NavigationSection.RESOURCE)
@UserRole(UserRole.VIEWER)
public final class SonarJStructuralDebtDashboard extends AbstractRubyTemplate implements RubyRailsWidget
{
    public String getId()
    {
        return "sonarj.structural_debt";
    }

    public String getTitle()
    {
        // not used for the moment by widgets.
        return "Structural Debt (SonarJ)";
    }

    protected String getTemplatePath()
    {
        return "/sonarj_debt_widget.html.erb";
    }
}