package com.hello2morrow.sonarplugin;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

/**
 * This class is the container for all others extensions
 */
@Properties
({
    @Property
    (
        key = SonarJSensor.LICENSE_FILE_NAME,
        defaultValue = "",
        name = "Path of SonarJ License File",
        project = false,
        module = false,
        global = true
    )
})

public final class SonarJPlugin implements Plugin
{
    public String getKey()
    {
        return SonarJPluginBase.PLUGIN_KEY;
    }

    public String getName()
    {
        return SonarJPluginBase.PLUGIN_KEY;
    }

    public String getDescription()
    {
        return "Plugin for hello2morrow's architecture management tool SonarJ";
    }

    public List<Class<? extends Extension>> getExtensions()
    {
        List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();

        list.add(SonarJMetrics.class);
        list.add(SonarJSensor.class);
        list.add(SonarJStructureDashboard.class);
        list.add(SonarJRulesRepository.class);
        return list;
    }

    @Override
    public String toString()
    {
        return getKey();
    }
}
