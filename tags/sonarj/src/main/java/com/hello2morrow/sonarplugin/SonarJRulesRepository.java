/*
 * Sonar SonarJ Plugin
 * Copyright (C) 2009, 2010 hello2morrow GmbH
 * mailto: info AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hello2morrow.sonarplugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.rules.Iso9126RulesCategories;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RulesRepository;

public final class SonarJRulesRepository implements RulesRepository<Java>
{
    public final static Rule ARCH = new Rule(SonarJPluginBase.PLUGIN_KEY, SonarJPluginBase.ARCH_RULE_KEY, "SonarJ Architecture Violation", Iso9126RulesCategories.MAINTAINABILITY, RulePriority.MAJOR);
    public final static Rule THRESHOLD = new Rule(SonarJPluginBase.PLUGIN_KEY, SonarJPluginBase.THRESHOLD_RULE_KEY, "SonarJ Threshold Violation", Iso9126RulesCategories.MAINTAINABILITY, RulePriority.MINOR);
    public final static Rule DUPLICATES = new Rule(SonarJPluginBase.PLUGIN_KEY, SonarJPluginBase.DUPLICATE_RULE_KEY, "SonarJ Duplicate Code Block", Iso9126RulesCategories.MAINTAINABILITY, RulePriority.MINOR);
    public final static Rule CYCLE_GROUPS = new Rule(SonarJPluginBase.PLUGIN_KEY, SonarJPluginBase.CYCLE_GROUP_RULE_KEY, "SonarJ Cycle Group", Iso9126RulesCategories.MAINTAINABILITY, RulePriority.MINOR);
    public final static Rule WORKSPACE = new Rule(SonarJPluginBase.PLUGIN_KEY, SonarJPluginBase.WORKSPACE_RULE_KEY, "SonarJ Workspace Warning", Iso9126RulesCategories.MAINTAINABILITY, RulePriority.MINOR);
    public final static Rule TASK = new Rule(SonarJPluginBase.PLUGIN_KEY, SonarJPluginBase.TASK_RULE_KEY, "SonarJ Task", Iso9126RulesCategories.MAINTAINABILITY, RulePriority.MINOR);

    
    public List<Rule> getInitialReferential()
    {
        return Arrays.asList(ARCH, THRESHOLD, TASK, CYCLE_GROUPS, WORKSPACE, DUPLICATES);
    }

    public Java getLanguage()
    {
        return Java.INSTANCE;
    }

    public List<RulesProfile> getProvidedProfiles()
    {
        return new ArrayList<RulesProfile>();
    }

    public List<Rule> parseReferential(String fileContent)
    {
        return new ArrayList<Rule>();
    }

}
