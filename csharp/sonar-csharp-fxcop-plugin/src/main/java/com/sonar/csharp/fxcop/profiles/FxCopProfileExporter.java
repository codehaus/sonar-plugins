/*
 * Sonar C# Plugin :: FxCop
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

package com.sonar.csharp.fxcop.profiles;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.profiles.ProfileExporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.utils.SonarException;

import com.sonar.csharp.fxcop.FxCopConstants;
import com.sonar.csharp.fxcop.profiles.utils.FxCopRule;
import com.sonar.csharp.fxcop.profiles.utils.XmlUtils;
import com.sonar.csharp.fxcop.profiles.xml.FxCopProject;
import com.sonar.csharp.fxcop.profiles.xml.ProjectOptions;
import com.sonar.csharp.fxcop.profiles.xml.RuleDef;
import com.sonar.csharp.fxcop.profiles.xml.RuleFile;
import com.sonar.csharp.fxcop.profiles.xml.RuleSet;

public class FxCopProfileExporter extends ProfileExporter {

  public FxCopProfileExporter() {
    super(FxCopConstants.REPOSITORY_KEY, FxCopConstants.PLUGIN_NAME);
    setSupportedLanguages(FxCopConstants.LANGUAGE_KEY);
    setMimeType("application/xml");
  }

  public void exportProfile(RulesProfile profile, Writer writer) {
    List<ActiveRule> activeRules = profile.getActiveRulesByRepository(FxCopConstants.REPOSITORY_KEY);
    List<FxCopRule> rules = buildRules(activeRules);
    String xmlModules = buildXmlFromRules(rules);
    try {
      writer.write(xmlModules);
    } catch (IOException e) {
      throw new SonarException("Fail to export the profile " + profile, e);
    }
  }

  /**
   * Builds all the FxCop rules from the active rules
   * 
   * @param activeRulesByPlugin
   * @return
   */
  private List<FxCopRule> buildRules(List<ActiveRule> activeRulesByPlugin) {
    List<FxCopRule> result = new ArrayList<FxCopRule>();

    for (ActiveRule activeRule : activeRulesByPlugin) {
      // Extracts the rule's date
      Rule rule = activeRule.getRule();
      String configKey = rule.getConfigKey();
      String fileName = StringUtils.substringAfter(configKey, "@");
      String name = StringUtils.substringBefore(configKey, "@");

      // Creates an populates the rule
      FxCopRule fxCopRule = new FxCopRule();
      fxCopRule.setCategory(rule.getRulesCategory().getName());
      fxCopRule.setEnabled(true);
      fxCopRule.setFileName(fileName);
      fxCopRule.setName(name);

      RulePriority priority = activeRule.getSeverity();
      if (priority != null) {
        fxCopRule.setPriority(priority.name().toLowerCase());
      }

      result.add(fxCopRule);
    }
    return result;
  }

  /**
   * Builds a FxCop rule file from the configured rules.
   * 
   * @param allRules
   * @return
   */
  private String buildXmlFromRules(List<FxCopRule> allRules) {
    FxCopProject report = new FxCopProject();
    Map<String, List<FxCopRule>> rulesByFile = new HashMap<String, List<FxCopRule>>();
    // We group the rules by filename
    for (FxCopRule fxCopRule : allRules) {
      String fileName = fxCopRule.getFileName();
      List<FxCopRule> rulesList = rulesByFile.get(fileName);
      if (rulesList == null) {
        rulesList = new ArrayList<FxCopRule>();
        rulesByFile.put(fileName, rulesList);
      }
      rulesList.add(fxCopRule);
    }

    // This is the main list
    List<RuleFile> ruleFiles = new ArrayList<RuleFile>();
    for (Map.Entry<String, List<FxCopRule>> fileEntry : rulesByFile.entrySet()) {
      RuleFile ruleFile = new RuleFile();
      ruleFile.setEnabled("True");

      // We copy all the rules informations
      ruleFile.setName(fileEntry.getKey());
      List<RuleDef> ruleDefinitions = new ArrayList<RuleDef>();
      List<FxCopRule> rules = fileEntry.getValue();
      for (FxCopRule fxCopRule : rules) {
        RuleDef currentRule = new RuleDef();
        currentRule.setName(fxCopRule.getName());
        currentRule.setPriority(fxCopRule.getPriority());
        ruleDefinitions.add(currentRule);
      }
      ruleFile.setRules(ruleDefinitions);
      ruleFiles.add(ruleFile);
    }

    RuleSet ruleSet = new RuleSet();
    ruleSet.setRules(ruleFiles);
    report.setProjectOptions(new ProjectOptions());
    report.setRules(ruleSet);

    CharArrayWriter writer = new CharArrayWriter();
    XmlUtils.marshall(report, writer);
    return writer.toString();
  }

}
