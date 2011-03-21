/*
 * Sonar C# Plugin :: StyleCop
 * Copyright (C) 2010 Jose Chillan, Alexandre Victoor and SonarSource
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

package org.sonar.plugins.csharp.stylecop.profiles;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.profiles.ProfileExporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.csharp.api.CSharpConstants;
import org.sonar.plugins.csharp.stylecop.StyleCopConstants;
import org.sonar.plugins.csharp.stylecop.profiles.utils.StyleCopRule;

/**
 * Class that allows to export a Sonar profile into a StyleCop rule definition file.
 */
public class StyleCopProfileExporter extends ProfileExporter {

  public StyleCopProfileExporter() {
    super(StyleCopConstants.REPOSITORY_KEY, StyleCopConstants.PLUGIN_NAME);
    setSupportedLanguages(CSharpConstants.LANGUAGE_KEY);
    setMimeType("application/xml");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void exportProfile(RulesProfile profile, Writer writer) {
    try {
      printStartOfFile(writer);

      printRules(profile, writer);

      printEndOfFile(writer);
    } catch (IOException e) {
      throw new SonarException("Error while generating the StyleCop profile to export: " + profile, e);
    }
  }

  private void printStartOfFile(Writer writer) throws IOException {
    writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
    writer.append("<StyleCopSettings Version=\"4.3\">\n");
    writer.append("    <Analyzers>\n");
  }

  private void printEndOfFile(Writer writer) throws IOException {
    writer.append("    </Analyzers>\n");
    writer.append("</StyleCopSettings>");
  }

  private void printRules(RulesProfile profile, Writer writer) throws IOException {
    List<ActiveRule> activeRules = profile.getActiveRulesByRepository(StyleCopConstants.REPOSITORY_KEY);
    List<StyleCopRule> rules = transformIntoStyleCopRules(activeRules);

    // We group the rules by RuleFile names
    Map<String, List<StyleCopRule>> rulesByFile = groupStyleCopRulesByAnalyzer(rules);
    // And then print out each rule
    for (String fileName : rulesByFile.keySet()) {
      printRuleFile(writer, rulesByFile, fileName);
    }
  }

  private List<StyleCopRule> transformIntoStyleCopRules(List<ActiveRule> activeRulesByPlugin) {
    List<StyleCopRule> result = new ArrayList<StyleCopRule>();

    for (ActiveRule activeRule : activeRulesByPlugin) {
      // Extracts the rule's information
      Rule rule = activeRule.getRule();
      String configKey = rule.getConfigKey();
      String analyzerName = StringUtils.substringAfter(configKey, "#");
      String name = StringUtils.substringBefore(configKey, "#");

      // Creates the StyleCop rule
      StyleCopRule fxCopRule = new StyleCopRule();
      fxCopRule.setEnabled(true);
      fxCopRule.setAnalyzerId(analyzerName);
      fxCopRule.setName(name);

      RulePriority priority = activeRule.getSeverity();
      if (priority != null) {
        fxCopRule.setPriority(priority.name().toLowerCase());
      }

      result.add(fxCopRule);
    }
    return result;
  }

  private Map<String, List<StyleCopRule>> groupStyleCopRulesByAnalyzer(List<StyleCopRule> rules) {
    Map<String, List<StyleCopRule>> rulesByAnalyzer = new HashMap<String, List<StyleCopRule>>();
    for (StyleCopRule fxCopRule : rules) {
      String analyzerId = fxCopRule.getAnalyzerId();
      List<StyleCopRule> rulesList = rulesByAnalyzer.get(analyzerId);
      if (rulesList == null) {
        rulesList = new ArrayList<StyleCopRule>();
        rulesByAnalyzer.put(analyzerId, rulesList);
      }
      rulesList.add(fxCopRule);
    }
    return rulesByAnalyzer;
  }

  private void printRuleFile(Writer writer, Map<String, List<StyleCopRule>> rulesByAnalyzer, String analyzerId) throws IOException {
    writer.append("        <Analyzer AnalyzerId=\"");
    StringEscapeUtils.escapeXml(writer, analyzerId);
    writer.append("\">\n");
    writer.append("            <Rules>\n");
    for (StyleCopRule fxCopRule : rulesByAnalyzer.get(analyzerId)) {
      printRule(writer, fxCopRule);
    }
    writer.append("            </Rules>\n");
    writer.append("        </Analyzer>\n");
  }

  private void printRule(Writer writer, StyleCopRule fxCopRule) throws IOException {
    writer.append("                <Rule Name=\"");
    StringEscapeUtils.escapeXml(writer, fxCopRule.getName());
    writer.append("\" SonarPriority=\"");
    StringEscapeUtils.escapeXml(writer, fxCopRule.getPriority());
    writer.append("\">\n");
    writer.append("                    <RuleSettings>\n");
    writer.append("                        <BooleanProperty Name=\"Enabled\">True</BooleanProperty>\n");
    writer.append("                    </RuleSettings>\n");
    writer.append("                </Rule>\n");
  }

}
