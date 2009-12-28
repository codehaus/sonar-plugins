/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.flex.flexpmd;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.IOUtils;

import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.SonarException;
import org.sonar.api.rules.*;
import org.sonar.plugins.flex.Flex;
import org.sonar.plugins.flex.FlexPlugin;
import org.sonar.plugins.flex.flexpmd.xml.Ruleset;
import org.sonar.plugins.flex.flexpmd.xml.Rule;
import org.sonar.plugins.flex.flexpmd.xml.Property;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlexPmdRulesRepository extends AbstractImportableRulesRepository<Flex, FlexPmdRulePriorityMapper> implements ConfigurationExportable {

  public FlexPmdRulesRepository(Flex language) {
    super(language, new FlexPmdRulePriorityMapper());
  }

  @Override
  public String getRepositoryResourcesBase() {
    return "org/sonar/plugins/flex/flexpmd";
  }

  @Override
  public Map<String, String> getBuiltInProfiles() {
    Map<String, String> defaults = new HashMap<String, String>();
    defaults.put("Default Flex Profile", "default-flex-profile.xml");
    return defaults;
  }

  public String exportConfiguration(RulesProfile activeProfile) {
    Ruleset tree = buildRulesetFromActiveProfile(activeProfile.getActiveRulesByPlugin(FlexPlugin.PLUGIN_KEY));
    String xmlModules = buildXmlFromRuleset(tree);
    return addHeaderToXml(xmlModules);
  }

  public List<ActiveRule> importConfiguration(String configuration, List<org.sonar.api.rules.Rule> rulesRepository) {
    Ruleset ruleSet = buildRuleSetFromXml(configuration);
    List<ActiveRule> activeRules = getActiveRulesFromRuleSet(ruleSet, rulesRepository);
    return activeRules;
  }

  protected Ruleset buildRuleSetFromXml(String configuration) {
    InputStream inputStream = null;
    try {
      XStream xstream = new XStream();
      xstream.processAnnotations(Ruleset.class);
      xstream.processAnnotations(org.sonar.api.rules.Rule.class);
      xstream.processAnnotations(Property.class);

      inputStream = IOUtils.toInputStream(configuration, "UTF-8");
      return (Ruleset) xstream.fromXML(inputStream);
    }
    catch (IOException e) {
      throw new SonarException("can't read configuration file", e);

    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  protected List<ActiveRule> getActiveRulesFromRuleSet(Ruleset ruleset, List<org.sonar.api.rules.Rule> rulesRepository) {
    List<ActiveRule> activeRules = new ArrayList<ActiveRule>();
    List<Rule> importedRules = ruleset.getRules();

    if (importedRules != null && !importedRules.isEmpty()) {
      for (Rule rule : importedRules) {
        String ref = rule.getRef();
        for (org.sonar.api.rules.Rule dbRule : rulesRepository) {
          if (dbRule.getConfigKey().equals(ref)) {
            RulePriority rulePriority = getRulePriorityMapper().from(rule.getPriority());
            ActiveRule activeRule = new ActiveRule(null, dbRule, rulePriority);
            activeRule.setActiveRuleParams(getActiveRuleParams(rule, dbRule, activeRule));
            activeRules.add(activeRule);
            break;
          }
        }
      }
    }
    return activeRules;
  }

  protected List<ActiveRuleParam> getActiveRuleParams(Rule rule, org.sonar.api.rules.Rule repositoryRule, ActiveRule activeRule) {
    List<ActiveRuleParam> activeRuleParams = new ArrayList<ActiveRuleParam>();
    if (rule.getProperties() != null) {
      for (Property property : rule.getProperties()) {
        if (repositoryRule.getParams() != null) {
          for (RuleParam ruleParam : repositoryRule.getParams()) {
            if (ruleParam.getKey().equals(property.getName())) {
              activeRuleParams.add(new ActiveRuleParam(activeRule, ruleParam, property.getValue()));
            }
          }
        }
      }
    }
    return activeRuleParams;
  }

  protected Ruleset buildRulesetFromActiveProfile(List<ActiveRule> activeRules) {
    Ruleset ruleset = new Ruleset("Sonar FlexPMD rules");
    for (ActiveRule activeRule : activeRules) {
      if (activeRule.getRule().getPluginName().equals(FlexPlugin.PLUGIN_KEY)) {
        String configKey = activeRule.getRule().getConfigKey();
        Rule rule = new Rule(configKey, getRulePriorityMapper().to(activeRule.getPriority()));
        List<Property> properties = null;
        if (activeRule.getActiveRuleParams() != null && !activeRule.getActiveRuleParams().isEmpty()) {
          properties = new ArrayList<Property>();
          for (ActiveRuleParam activeRuleParam : activeRule.getActiveRuleParams()) {
            properties.add(new Property(activeRuleParam.getRuleParam().getKey(), activeRuleParam.getValue()));
          }
        }
        rule.setProperties(properties);
        ruleset.addRule(rule);
      }
    }
    return ruleset;
  }

  protected String buildXmlFromRuleset(Ruleset tree) {
    XStream xstream = new XStream();
    xstream.processAnnotations(Ruleset.class);
    xstream.processAnnotations(Rule.class);
    xstream.processAnnotations(Property.class);
    return xstream.toXML(tree);
  }


  protected String addHeaderToXml(String xmlModules) {
    String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    return header + xmlModules;
  }
}
