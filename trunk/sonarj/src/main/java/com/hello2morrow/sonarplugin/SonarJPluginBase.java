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

public final class SonarJPluginBase
{
    public final static String PLUGIN_KEY = "SonarJ";
    public final static String ARCH_RULE_KEY = "sonarj.architecture";
    public final static String THRESHOLD_RULE_KEY = "sonarj.threshold";
    public final static String DUPLICATE_RULE_KEY = "sonarj.duplicate";
    public final static String CYCLE_GROUP_RULE_KEY = "sonarj.cyclegroup";
    public final static String WORKSPACE_RULE_KEY = "sonarj.workspace";
    public final static String TASK_RULE_KEY = "sonarj.open_task";
    
    private SonarJPluginBase()
    {
        // Don't instantiate
    }
}
