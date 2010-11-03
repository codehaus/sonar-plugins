/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
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

import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CountDistributionBuilder;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.*;
import org.sonar.api.utils.ParsingUtils;
import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.XpathParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class TaglistViolationsXmlParser {

  private RulesManager rulesManager;
  private RulesProfile rulesProfile;

  protected TaglistViolationsXmlParser(RulesManager rulesManager, RulesProfile rulesProfile) {
    this.rulesManager = rulesManager;
    this.rulesProfile = rulesProfile;
  }

  protected final void populateTaglistViolation(SensorContext context, File taglistXmlFile) throws IOException {
    XpathParser parser = new XpathParser();

    parser.parse(taglistXmlFile);

    NodeList tags = parser.getDocument().getElementsByTagName("tag");
    Map<Resource, ViolationsCount> violationsCountPerClass = new HashMap<Resource, ViolationsCount>();

    for (int i = 0; i < tags.getLength(); i++) {
      Element tag = (Element) tags.item(i);
      String tagName = tag.getAttribute("name");
      Rule rule = rulesManager.getPluginRule(TaglistPlugin.KEY, tagName);
      ActiveRule activeRule = rulesProfile.getActiveRule(TaglistPlugin.KEY, tagName);
      if (activeRule != null && rule != null) {
        parseViolationsOnFiles(context, tag, rule, activeRule, violationsCountPerClass);
      }
    }

    for (Map.Entry<Resource, ViolationsCount> entry : violationsCountPerClass.entrySet()) {
      Resource javaFile = entry.getKey();
      ViolationsCount violations = entry.getValue();
      saveMeasure(context, javaFile, TaglistMetrics.TAGS, violations.mandatory + violations.optional);
      saveMeasure(context, javaFile, TaglistMetrics.MANDATORY_TAGS, violations.mandatory);
      saveMeasure(context, javaFile, TaglistMetrics.OPTIONAL_TAGS, violations.optional);
      context.saveMeasure(javaFile, violations.distrib.build().setPersistenceMode(PersistenceMode.MEMORY));
    }
  }

  private void saveMeasure(SensorContext context, Resource javaFile, Metric metric, int value) {
    if (value > 0) {
      context.saveMeasure(javaFile, metric, (double) value);
    }
  }

  private void parseViolationsOnFiles(SensorContext context, Element tag, Rule rule, ActiveRule activeRule, Map
      <Resource, ViolationsCount> violationsCountPerClass) {
    NodeList files = tag.getElementsByTagName("file");
    for (int i = 0; i < files.getLength(); i++) {
      Element file = (Element) files.item(i);
      String className = file.getAttribute("name");
      // see SONARPLUGINS-57
      className = className.startsWith("null.") ? className.substring(5) : className;

      Resource javaFile = new JavaFile(className);
      int violationsForClass = parseViolationLineNumberAndComment(context, file, javaFile, rule);
      ViolationsCount violationsCount = violationsCountPerClass.get(javaFile);
      if (violationsCount == null) {
        violationsCount = new ViolationsCount();
        violationsCountPerClass.put(javaFile, violationsCount);
      }

      if (activeRule.getPriority().equals(RulePriority.BLOCKER) || activeRule.getPriority().equals(RulePriority.CRITICAL)) {
        violationsCount.mandatory += violationsForClass;
      } else {
        violationsCount.optional += violationsForClass;
      }
      violationsCount.distrib.add(rule.getKey());
    }
  }

  private int parseViolationLineNumberAndComment(SensorContext context, Element file, Resource javaFile, Rule rule) {
    int createdViolations = 0;
    NodeList comments = file.getElementsByTagName("comment");
    for (int i = 0; i < comments.getLength(); i++) {
      Element comment = (Element) comments.item(i);
      if (comment.getElementsByTagName("lineNumber").getLength() > 0) {
        String violationLineNumber = comment.getElementsByTagName("lineNumber").item(0).getTextContent();
        registerViolation(context, violationLineNumber, rule, javaFile);
        createdViolations++;
      }
    }
    return createdViolations;
  }

  private void registerViolation(SensorContext context, String violationLineNumber, Rule rule, Resource javaFile) {
    try {
      context.saveViolation(new Violation(rule, javaFile)
          .setMessage(rule.getDescription())
          .setLineId((int) ParsingUtils.parseNumber(violationLineNumber))
      );

    } catch (ParseException e) {
      throw new SonarException("Unable to parse number '" + violationLineNumber + "' in taglist.xml file", e);
    }
  }

  private class ViolationsCount {
    private int mandatory;
    private int optional;
    private CountDistributionBuilder distrib = new CountDistributionBuilder(TaglistMetrics.TAGS_DISTRIBUTION);
  }
}
