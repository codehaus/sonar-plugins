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
    public final static Rule TASK = new Rule(SonarJPluginBase.PLUGIN_KEY, SonarJPluginBase.TASK_RULE_KEY, "SonarJ Task", Iso9126RulesCategories.MAINTAINABILITY, RulePriority.MINOR);

    static
    {
        ARCH.setConfigKey(SonarJPluginBase.ARCH_RULE_KEY);
        THRESHOLD.setConfigKey(SonarJPluginBase.THRESHOLD_RULE_KEY);
        TASK.setConfigKey(SonarJPluginBase.TASK_RULE_KEY);
    }
    
    public List<Rule> getInitialReferential()
    {
        return Arrays.asList(ARCH, THRESHOLD, TASK);
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
