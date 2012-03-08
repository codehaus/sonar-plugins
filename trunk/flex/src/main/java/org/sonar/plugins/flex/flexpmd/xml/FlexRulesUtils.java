/*
 * Sonar Flex Plugin
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.flex.flexpmd.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ActiveRuleParam;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleParam;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.flex.flexpmd.FlexPmdConstants;

import com.thoughtworks.xstream.XStream;

public final class FlexRulesUtils {

  private static Properties ruleMessages;

  static {
    ruleMessages = new Properties();
    InputStream inputStream = null;
    try {
      inputStream = FlexRulesUtils.class.getResourceAsStream("rule-messages.properties");
      ruleMessages.load(inputStream);
    } catch (IOException e) {
      throw new SonarException("Can't load the 'rule-messages.properties' file.", e);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  private FlexRulesUtils() {
  }

  public static Ruleset buildRuleSetFromXml(String configuration) {
    XStream xstream = new XStream();
    xstream.setClassLoader(FlexRulesUtils.class.getClassLoader());
    xstream.processAnnotations(Ruleset.class);
    xstream.processAnnotations(FlexRule.class);
    xstream.processAnnotations(Property.class);
    xstream.aliasSystemAttribute("ref", "class");

    return (Ruleset) xstream.fromXML(configuration);
  }

  public static String buildXmlFromRuleset(Ruleset tree) {
    XStream xstream = new XStream();
    xstream.setClassLoader(FlexRulesUtils.class.getClassLoader());
    xstream.processAnnotations(Ruleset.class);
    xstream.processAnnotations(FlexRule.class);
    xstream.processAnnotations(Property.class);
    return addHeaderToXml(xstream.toXML(tree));
  }

  private static String addHeaderToXml(String xmlModules) {
    String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    return header + xmlModules;
  }

  public static String getConfigurationFromFile(String path) {
    InputStream inputStream = Ruleset.class.getResourceAsStream(path);
    String configuration = null;
    try {
      configuration = IOUtils.toString(inputStream, "UTF-8");
    } catch (IOException e) {
      throw new SonarException("Unable to read configuration file for the profile : " + path, e);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
    return configuration;
  }

  private static final String RESOURCE_PATH = "/org/sonar/plugins/flex/flexpmd/";

  public static List<Rule> getInitialReferential() {
    return parseReferential(RESOURCE_PATH + "rules.xml");
  }

  public static List<Rule> parseReferential(String path) {
    Ruleset ruleset = FlexRulesUtils.buildRuleSetFromXml(FlexRulesUtils.getConfigurationFromFile(path));
    List<Rule> rulesRepository = new ArrayList<Rule>();
    for (FlexRule fRule : ruleset.getRules()) {
      rulesRepository.add(createRepositoryRule(fRule));
    }
    return rulesRepository;
  }

  public static List<ActiveRule> importConfiguration(String configuration, List<Rule> rulesRepository) {
    Ruleset ruleset = FlexRulesUtils.buildRuleSetFromXml(configuration);
    List<ActiveRule> activeRules = new ArrayList<ActiveRule>();
    for (FlexRule fRule : ruleset.getRules()) {
      ActiveRule activeRule = createActiveRule(fRule, rulesRepository);
      if (activeRule != null) {
        activeRules.add(activeRule);
      }
    }
    return activeRules;
  }

  public static String exportConfiguration(RulesProfile activeProfile) {
    Ruleset tree = buildRulesetFromActiveProfile(activeProfile.getActiveRulesByRepository(FlexPmdConstants.REPOSITORY_KEY));
    return FlexRulesUtils.buildXmlFromRuleset(tree);
  }

  private static Rule createRepositoryRule(FlexRule fRule) {
    RulePriority priority = severityFromLevel(fRule.getPriority());
    String ruleName = computeRuleNameFromClassAttribute(fRule.getClazz());
    Rule rule = Rule.create(FlexPmdConstants.REPOSITORY_KEY, fRule.getClazz(), ruleName).setSeverity(priority);
    rule.setDescription(fRule.getDescription());
    List<RuleParam> ruleParams = new ArrayList<RuleParam>();
    if (fRule.getProperties() != null) {
      for (Property property : fRule.getProperties()) {
        RuleParam param = rule.createParameter()
            .setKey(property.getName())
            .setDescription(property.getName())
            .setType("i");
        ruleParams.add(param);
      }
    }
    rule.setParams(ruleParams);
    return rule;
  }

  protected static String computeRuleNameFromClassAttribute(String nameAttribute) {
    StringBuilder name = new StringBuilder();
    String className = StringUtils.substringAfterLast(nameAttribute, ".");
    boolean first = true;
    for (char character : className.toCharArray()) {
      if (Character.isUpperCase(character) && !first) {
        name.append(" ");
        name.append(Character.toLowerCase(character));
      } else if (Character.isDigit(character)) {
        name.append(" ");
        name.append(character);
      } else {
        name.append(character);
      }
      first = false;
    }
    return name.toString();
  }

  private static ActiveRule createActiveRule(FlexRule fRule, List<Rule> rulesRepository) {
    String clazz = fRule.getClazz();
    RulePriority fRulePriority = severityFromLevel(fRule.getPriority());
    for (Rule rule : rulesRepository) {
      if (rule.getKey().equals(clazz)) {
        RulePriority priority = fRulePriority != null ? fRulePriority : rule.getSeverity();
        ActiveRule activeRule = new ActiveRule(null, rule, priority);
        activeRule.setActiveRuleParams(buildActiveRuleParams(fRule, rule, activeRule));
        return activeRule;
      }
    }
    return null;
  }

  static List<ActiveRuleParam> buildActiveRuleParams(FlexRule flexRule, Rule repositoryRule, ActiveRule activeRule) {
    List<ActiveRuleParam> activeRuleParams = new ArrayList<ActiveRuleParam>();
    if (flexRule.getProperties() != null) {
      for (Property property : flexRule.getProperties()) {
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

  static Ruleset buildRulesetFromActiveProfile(List<ActiveRule> activeRules) {
    Ruleset ruleset = new Ruleset();
    for (ActiveRule activeRule : activeRules) {
      if (activeRule.getRule().getRepositoryKey().equals(FlexPmdConstants.REPOSITORY_KEY)) {
        String key = activeRule.getRule().getKey();
        String priority = severityToLevel(activeRule.getSeverity());
        FlexRule flexRule = new FlexRule(key, priority);
        List<Property> properties = new ArrayList<Property>();
        for (ActiveRuleParam activeRuleParam : activeRule.getActiveRuleParams()) {
          properties.add(new Property(activeRuleParam.getRuleParam().getKey(), activeRuleParam.getValue()));
        }
        flexRule.setProperties(properties);
        flexRule.setMessage(findMessageForRule(activeRule.getConfigKey()));
        ruleset.addRule(flexRule);
      }
    }
    return ruleset;
  }

  static String findMessageForRule(String activeRuleConfigKey) {
    return ruleMessages.getProperty(activeRuleConfigKey);
  }

  private static RulePriority severityFromLevel(String level) {
    if ("1".equals(level)) {
      return RulePriority.BLOCKER;
    }
    if ("2".equals(level)) {
      return RulePriority.CRITICAL;
    }
    if ("3".equals(level)) {
      return RulePriority.MAJOR;
    }
    if ("4".equals(level)) {
      return RulePriority.MINOR;
    }
    if ("5".equals(level)) {
      return RulePriority.INFO;
    }
    return null;
  }

  private static String severityToLevel(RulePriority priority) {
    if (priority.equals(RulePriority.BLOCKER)) {
      return "1";
    }
    if (priority.equals(RulePriority.CRITICAL)) {
      return "2";
    }
    if (priority.equals(RulePriority.MAJOR)) {
      return "3";
    }
    if (priority.equals(RulePriority.MINOR)) {
      return "4";
    }
    if (priority.equals(RulePriority.INFO)) {
      return "5";
    }
    throw new IllegalArgumentException("Level not supported: " + priority);
  }

}
