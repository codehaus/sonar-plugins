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
package org.sonar.plugins.taglist;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.sonar.api.rules.RulesManager;
import org.sonar.commons.rules.RulesProfile;
import org.sonar.commons.rules.ActiveRule;
import org.sonar.commons.rules.Rule;
import org.sonar.commons.rules.RulePriority;


import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.Project;
import org.sonar.api.batch.Resource;
import org.sonar.api.batch.JavaClass;
import org.sonar.api.batch.measures.PropertiesBuilder;
import org.sonar.api.utils.XpathParser;
import org.sonar.api.utils.ParsingUtils;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.apache.commons.io.FileUtils;
import sun.net.www.ParseUtil;

public class TaglistViolationsXmlParser {

  private RulesManager rulesManager;
  private RulesProfile rulesProfile;
  
  protected TaglistViolationsXmlParser(RulesManager rulesManager, RulesProfile rulesProfile) {
    this.rulesManager = rulesManager;
    this.rulesProfile = rulesProfile;
  }

  protected final void populateTaglistViolation(SensorContext context, Project pom, File taglistXmlFile) throws IOException {

    XpathParser parser = new XpathParser();
    // TODO remove when MTAGLIST-40 released
    String charSet = pom.getSourceCharset().name();
    String report = FileUtils.readFileToString(taglistXmlFile, charSet);
    parser.parse(report.replace("encoding=\"UTF-8\"", "encoding=\""+charSet+"\""));

    NodeList tags = parser.getDocument().getElementsByTagName("tag");
    Map<Resource, ViolationsCount> violationsCountPerClass = new HashMap<Resource, ViolationsCount>();

    for (int i = 0; i < tags.getLength(); i++) {
      Element tag = (Element) tags.item(i);
      String tagName = tag.getAttribute("name");
      Rule rule = rulesManager.getPluginRule(TaglistPlugin.KEY, tagName);
      ActiveRule activeRule = rulesProfile.getActiveRule(TaglistPlugin.KEY, tagName);
      if (activeRule != null && rule != null) {
        parseViolationsOnFiles(context, tag, tagName, rule, activeRule, violationsCountPerClass);
      }
    }

    for (Resource javaClass : violationsCountPerClass.keySet()) {
      ViolationsCount violations = violationsCountPerClass.get(javaClass);
      context.saveMeasure(javaClass, TaglistMetrics.TAGS, (double) violations.mandatory + violations.optional);
      context.saveMeasure(javaClass, TaglistMetrics.MANDATORY_TAGS, (double) violations.mandatory);
      context.saveMeasure(javaClass, TaglistMetrics.OPTIONAL_TAGS, (double) violations.optional);
    }
  }

  private void parseViolationsOnFiles(SensorContext context, Element tag, String tagName, Rule rule, ActiveRule activeRule, Map<Resource, ViolationsCount> violationsCountPerClass) {
    NodeList files = tag.getElementsByTagName("file");
    int totalViolationsForTag = 0;
    for (int i = 0; i < files.getLength(); i++) {
      Element file = (Element) files.item(i);
      String className = file.getAttribute("name");
      // see SONARPLUGINS-57
      className = className.startsWith("null.") ? className.substring(5) : className;
      // exclude unit tests not a really great way to do it.. unfortunatly the only one as long as MTAGLIST-41 is not done
      // TODO integrate MTAGLIST-41 if done one day

      if (className.toLowerCase().startsWith("test") || className.toLowerCase().endsWith("test")) continue;
      
      Resource javaClass = new JavaClass(className);
      int violationsForClass = parseViolationLineNumberAndComment(context, file, javaClass, tagName, rule, activeRule);
      totalViolationsForTag += violationsForClass;
      ViolationsCount violationsCount = violationsCountPerClass.get(javaClass);
      if (violationsCount == null) {
        violationsCount = new ViolationsCount();
        violationsCountPerClass.put(javaClass, violationsCount);
      }
      
      if (activeRule.getLevel().equals(RulePriority.BLOCKER) || activeRule.getLevel().equals(RulePriority.CRITICAL)) {
        violationsCount.mandatory += violationsForClass;
      } else {
        violationsCount.optional += violationsForClass;
      }
    }
  }

  private int parseViolationLineNumberAndComment(SensorContext context, Element file, Resource javaClass, String tagName, Rule rule, ActiveRule activeRule) {
    int createdViolations = 0;
    NodeList comments = file.getElementsByTagName("comment");
    for (int i = 0; i < comments.getLength(); i++) {
      Element comment = (Element) comments.item(i);
      if (comment.getElementsByTagName("lineNumber").getLength() > 0) {
        String violationLineNumber = comment.getElementsByTagName("lineNumber").item(0).getTextContent();
        registerViolation(context, tagName, violationLineNumber, file, rule, activeRule, javaClass);
        createdViolations++;
      }
    }
    return createdViolations;
  }

  private void registerViolation(SensorContext context, String tagName, String violationLineNumber, Element file, Rule rule, ActiveRule activeRule, Resource javaClass) {
    try {
      RulePriority level = activeRule.getLevel();
      context.saveViolation(javaClass, rule, rule.getDescription(), level, (int)ParsingUtils.parseNumber(violationLineNumber));
    } catch (ParseException e) {
      throw new RuntimeException("Unable to parse number '" + violationLineNumber + "' in taglist.xml file", e);
    }
  }

  private class ViolationsCount {
    private int mandatory;
    private int optional;
  }
}
