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

import org.sonar.commons.Metric;
import org.sonar.commons.resources.Resource;
import org.sonar.commons.rules.ActiveRule;
import org.sonar.commons.rules.Rule;
import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.Java;
import static org.sonar.plugins.api.maven.MavenCollectorUtils.parseNumber;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.xml.XpathParser;
import org.sonar.plugins.api.rules.RulesManager;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.text.ParseException;
import java.util.Set;

public class TaglistViolationsXmlParser {

    private XpathParser parser = new XpathParser();
    private RulesManager rulesManager;
    private RulesProfile rulesProfile;
    private ProjectContext context;
    private Set<String> tagsToDisplayInDashboard;

    protected TaglistViolationsXmlParser(ProjectContext context, RulesManager rulesManager, RulesProfile rulesProfile,
                                         Set<String> tagsToDisplayInDashboard) {
        this.context = context;
        this.rulesManager = rulesManager;
        this.rulesProfile = rulesProfile;
        this.tagsToDisplayInDashboard = tagsToDisplayInDashboard;
    }

    protected final void populateTaglistViolation(File taglistXmlFile) {
        parser.parse(taglistXmlFile);
        parseViolationsOnTags();
    }

    private void parseViolationsOnTags() {
        NodeList tags = parser.getDocument().getElementsByTagName("tag");
        if (tags != null) {
            for (int i = 0; i < tags.getLength(); i++) {
                Element tag = (Element) tags.item(i);
                parseViolationsOnFiles(tag, tag.getAttribute("name"));
            }
        }
    }

    private void parseViolationsOnFiles(Element tag, String tagName) {
        NodeList files = tag.getElementsByTagName("file");
        if (files != null) {
            for (int i = 0; i < files.getLength(); i++) {
                Element file = (Element) files.item(i);
                String fileName = file.getAttribute("name");
                if (tagsToDisplayInDashboard.contains(tagName)) {
                    double tagViolations;
                    try {
                        tagViolations = parseNumber(file.getAttribute("count"));
                    } catch (ParseException e) {
                        throw new IllegalStateException("Unable to parse count attribute '"
                                + file.getAttribute("count") + "' on tag " + tagName + " nin taglist.xml file", e);
                    }
                    context.addMeasure(Java.newClass(fileName), new Metric(tagName), tagViolations);
                }
                parseViolationLineNumberAndComment(file, fileName, tagName);
            }
        }
    }

    private void parseViolationLineNumberAndComment(Element file, String fileName, String tagName) {
        NodeList comments = file.getElementsByTagName("comment");
        if (comments != null) {
            for (int i = 0; i < comments.getLength(); i++) {
                Element comment = (Element) comments.item(i);
                if (comment.getElementsByTagName("lineNumber").getLength() > 0) {
                    String violationLineNumber = comment.getElementsByTagName("lineNumber").item(0).getTextContent();
                    registerViolation(tagName, fileName, violationLineNumber);
                }
            }
        }
    }

    private void registerViolation(String tagName, String fileName, String violationLineNumber) {
        Rule rule = rulesManager.getPluginRule(TaglistPlugin.KEY, tagName);
        ActiveRule activeRule = rulesProfile.getActiveRule(TaglistPlugin.KEY, tagName);
        if (activeRule != null) {
            try {
                Resource javaFile = Java.newClass(fileName);
                if (rule != null && javaFile != null) {
                    context.addViolation(javaFile, rule, rule.getDescription(), activeRule.getLevel(), (int) parseNumber(violationLineNumber));
                }
            } catch (ParseException e) {
                throw new IllegalStateException("Unable to parse number '" + violationLineNumber + "' in taglist.xml file", e);
            }
        }

    }
}
