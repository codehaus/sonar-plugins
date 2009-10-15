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
    public final static Rule ARCH = new Rule(SonarJPluginBase.PLUGIN_KEY, SonarJPluginBase.ARCH_RULE_KEY, "SonarJ Architecture Constraints", new RulesCategory("Maintainability"), RulePriority.MAJOR);

    static
    {
        ARCH.setConfigKey(SonarJPluginBase.ARCH_RULE_KEY);
    }
    
    public List<Rule> getInitialReferential()
    {
        return Arrays.asList(ARCH);
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
