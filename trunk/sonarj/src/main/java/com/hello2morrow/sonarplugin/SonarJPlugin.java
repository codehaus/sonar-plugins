/*
 * Sonar-SonarJ-Plugin
 * Open source plugin for Sonar
 * Copyright (C) 2009, 2010 hello2morrow GmbH
 * mailto: info AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
    ),
    
    @Property
    (
        key = SonarJSensor.DEVELOPER_COST_PER_HOUR,
        defaultValue = "70",
        name = "Cost per Developer Hour in US$",
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
        list.add(SonarJRulesRepository.class);
        list.add(SonarJStructuralDebtDashboard.class);
        list.add(SonarJCyclicityDashboard.class);
        list.add(SonarJArchitectureDashboard.class);
        list.add(SonarJMetricAggregator.class);
        return list;
    }

    @Override
    public String toString()
    {
        return getKey();
    }
}
