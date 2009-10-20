package com.hello2morrow.sonarplugin;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.RubyRailsWidget;

public class SonarJArchitectureDashboard extends AbstractRubyTemplate implements RubyRailsWidget
{

    @Override
    protected String getTemplatePath()
    {
        return "/sonarj_architecture_widget.html.erb";
    }

    public String getId()
    {
        return "sonarj.architecture";
    }

    public String getTitle()
    {
        return "SonarJ Architecture Dashbox";
    }

}
