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

import static org.sonar.plugins.api.maven.MavenCollectorUtils.parseNumber;

import java.io.File;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.sonar.commons.resources.Resource;
import org.sonar.commons.rules.ActiveRule;
import org.sonar.commons.rules.Rule;
import org.sonar.commons.rules.RuleFailureLevel;
import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.xml.XpathParser;
import org.sonar.plugins.api.measures.PropertiesBuilder;
import org.sonar.plugins.api.rules.RulesManager;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TaglistViolationsXmlParser {

  private XpathParser parser = new XpathParser();
  private RulesManager rulesManager;
  private RulesProfile rulesProfile;
  private ProjectContext context;
  protected TaglistViolationsXmlParser(ProjectContext context, RulesManager rulesManager, RulesProfile rulesProfile) {
    this.context = context;
    this.rulesManager = rulesManager;
    this.rulesProfile = rulesProfile;
  }

  protected final void populateTaglistViolation(File taglistXmlFile) {
    parser.parse(taglistXmlFile);
    PropertiesBuilder tagsDistrib = new PropertiesBuilder(TaglistMetrics.TAGS_DISTRIBUTION);
    NodeList tags = parser.getDocument().getElementsByTagName("tag");
    Map<Resource, ViolationsCount> violationsCountPerClass = new HashMap<Resource, ViolationsCount>();
    for (int i = 0; i < tags.getLength(); i++) {
      Element tag = (Element) tags.item(i);
      String tagName = tag.getAttribute("name");
      Rule rule = rulesManager.getPluginRule(TaglistPlugin.KEY, tagName);
      ActiveRule activeRule = rulesProfile.getActiveRule(TaglistPlugin.KEY, tagName);
      if (activeRule != null && rule != null) {
        int violationsForTag = parseViolationsOnFiles(tag, tagName, rule, activeRule, violationsCountPerClass);
        tagsDistrib.add(tagName, violationsForTag);
      }
    }
    context.addMeasure(tagsDistrib.build());
    for (Resource javaClass : violationsCountPerClass.keySet()) {
      ViolationsCount violations = violationsCountPerClass.get(javaClass);
      context.addMeasure(javaClass, TaglistMetrics.TAGS, new Double(violations.mandatory + violations.optional));
      context.addMeasure(javaClass, TaglistMetrics.MANDATORY_TAGS, new Double(violations.mandatory));
      context.addMeasure(javaClass, TaglistMetrics.OPTIONAL_TAGS, new Double(violations.optional));
    }
  }

  private int parseViolationsOnFiles(Element tag, String tagName, Rule rule, ActiveRule activeRule, Map<Resource, ViolationsCount> violationsCountPerClass) {
    NodeList files = tag.getElementsByTagName("file");
    int totalViolationsForTag = 0;
    for (int i = 0; i < files.getLength(); i++) {
      Element file = (Element) files.item(i);
      String className = file.getAttribute("name");
      // see SONARPLUGINS-57
      className = className.startsWith("null.") ? className.substring(5) : className;
      Resource javaClass = Java.newClass(className);
      int violationsForClass = parseViolationLineNumberAndComment(file, javaClass, tagName, rule, activeRule);
      totalViolationsForTag += violationsForClass;
      ViolationsCount violationsCount = violationsCountPerClass.get(javaClass);
      if (violationsCount == null) {
        violationsCount = new ViolationsCount();
        violationsCountPerClass.put(javaClass, violationsCount);
      }
      
      if (activeRule.getLevel().equals(RuleFailureLevel.ERROR)) {
        violationsCount.mandatory += violationsForClass;
      } else if (activeRule.getLevel().equals(RuleFailureLevel.WARNING)) {
        violationsCount.optional += violationsForClass;
      }
    }
    return totalViolationsForTag;
  }

  private int parseViolationLineNumberAndComment(Element file, Resource javaClass, String tagName, Rule rule, ActiveRule activeRule) {
    int createdViolations = 0;
    NodeList comments = file.getElementsByTagName("comment");
    for (int i = 0; i < comments.getLength(); i++) {
      Element comment = (Element) comments.item(i);
      if (comment.getElementsByTagName("lineNumber").getLength() > 0) {
        String violationLineNumber = comment.getElementsByTagName("lineNumber").item(0).getTextContent();
        registerViolation(tagName, violationLineNumber, file, rule, activeRule, javaClass);
        createdViolations++;
      }
    }
    return createdViolations;
  }

  private void registerViolation(String tagName, String violationLineNumber, Element file, Rule rule, ActiveRule activeRule, Resource javaClass) {
    try {
      RuleFailureLevel level = activeRule.getLevel();
      context.addViolation(javaClass, rule, rule.getDescription(), level, (int) parseNumber(violationLineNumber));
    } catch (ParseException e) {
      throw new RuntimeException("Unable to parse number '" + violationLineNumber + "' in taglist.xml file", e);
    }
  }
  
  private class ViolationsCount {
    
    private int mandatory;
    private int optional;

  }
}
