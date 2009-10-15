package com.hello2morrow.sonarplugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RulesCategory;
import org.sonar.api.rules.RulesRepository;

public final class SonarJRulesRepository implements RulesRepository<Java>
{
    public final static Rule ARCH = new Rule(SonarJPluginBase.PLUGIN_KEY, SonarJPluginBase.ARCH_RULE_KEY, "SonarJ Architecture Violation", new RulesCategory("Maintainability"), RulePriority.MAJOR);
    public final static Rule THRESHOLD = new Rule(SonarJPluginBase.PLUGIN_KEY, SonarJPluginBase.THRESHOLD_RULE_KEY, "SonarJ Threshold Violation", new RulesCategory("Maintainability"), RulePriority.MINOR);
    public final static Rule TASK_LOW = new Rule(SonarJPluginBase.PLUGIN_KEY, SonarJPluginBase.TASK_LOW_RULE_KEY, "SonarJ Task (Low Priority)", new RulesCategory("Maintainability"), RulePriority.INFO);
    public final static Rule TASK_MEDIUM = new Rule(SonarJPluginBase.PLUGIN_KEY, SonarJPluginBase.TASK_MEDIUM_RULE_KEY, "SonarJ Task (Medium Priority)", new RulesCategory("Maintainability"), RulePriority.MINOR);
    public final static Rule TASK_HIGH = new Rule(SonarJPluginBase.PLUGIN_KEY, SonarJPluginBase.TASK_HIGH_RULE_KEY, "SonarJ Task (High Priority)", new RulesCategory("Maintainability"), RulePriority.MAJOR);

    static
    {
        ARCH.setConfigKey(SonarJPluginBase.ARCH_RULE_KEY);
        THRESHOLD.setConfigKey(SonarJPluginBase.THRESHOLD_RULE_KEY);
        TASK_LOW.setConfigKey(SonarJPluginBase.TASK_LOW_RULE_KEY);
        TASK_MEDIUM.setConfigKey(SonarJPluginBase.TASK_MEDIUM_RULE_KEY);
        TASK_HIGH.setConfigKey(SonarJPluginBase.TASK_HIGH_RULE_KEY);
    }
    
    public List<Rule> getInitialReferential()
    {
        return Arrays.asList(ARCH, THRESHOLD, TASK_LOW, TASK_MEDIUM, TASK_HIGH);
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
