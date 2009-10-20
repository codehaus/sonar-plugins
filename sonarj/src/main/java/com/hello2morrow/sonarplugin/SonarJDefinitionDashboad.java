package com.hello2morrow.sonarplugin;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.RubyRailsWidget;

public class SonarJDefinitionDashboad extends AbstractRubyTemplate implements RubyRailsWidget
{

    @Override
    protected String getTemplatePath()
    {
        return "/sonarj_definition_widget.html.erb";
    }

    public String getId()
    {
        return "sonarj.definition_dashboard";
    }

    public String getTitle()
    {
        return "SonarJ Architecture Definition Dashbox";
    }

}
