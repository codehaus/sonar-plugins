/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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

package org.sonar.plugins.jlint;

import com.thoughtworks.xstream.XStream;
import org.sonar.plugins.jlint.xml.CategoryMap;
import org.sonar.plugins.jlint.xml.CategoryRuleMap;
import org.sonar.plugins.jlint.xml.JlintFilter;
import org.sonar.plugins.jlint.xml.Match;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JlintConfiguration {
  private static final String CATEGORY_MAP_FILE = "/org/sonar/plugins/jlint/category_rule_map.xml";

  private JlintFilter filter;
  private CategoryRuleMap categoryRuleMap;


  public JlintConfiguration() {
    loadMapFile();
  }

  public JlintConfiguration(JlintFilter filter) {
    this.filter = filter;
    loadMapFile();
  }

  private void loadMapFile() {
    InputStream input = getClass().getResourceAsStream(CATEGORY_MAP_FILE);

    if (input == null) {
      //DEBUG
      System.out.println("Cannot load " + CATEGORY_MAP_FILE + " file.");
      System.exit(-1);
    }

    categoryRuleMap = new CategoryRuleMap();

    XStream xstream = new XStream();
    xstream.alias("CategoryRuleMap", CategoryRuleMap.class);
    xstream.alias("category", CategoryMap.class);
    xstream.aliasAttribute(CategoryMap.class, "name", "name");
    xstream.alias("rule", String.class);
    xstream.addImplicitCollection(CategoryRuleMap.class, "categories");

    xstream.fromXML(input, categoryRuleMap);

  }

  public String toXml() {
    Set<String> enabledCategories = getAllowedCategories();

    StringBuilder xml = new StringBuilder();

    xml.append("<JlintRules>\n");
    for (String category : enabledCategories) {
      xml.append("<Rule>");
      xml.append(category);
      xml.append("</Rule>\n");
    }
    xml.append("</JlintRules>\n");

    return xml.toString();
  }

  private Set<String> getAllowedCategories() {
    String category;
    String ruleCode;

    Set<String> categories = new HashSet<String>();
    ;

    List<Match> matchs = filter.getMatchs();

    for (Match match : matchs) {
      ruleCode = getCategoryForRule(match.getBug().getPattern());
      categories.add(ruleCode);
    }

    return categories;

  }

  private String getCategoryForRule(String rule) {
    for (CategoryMap categoryMap : categoryRuleMap.getCategories()) {

      for (String mapRule : categoryMap.getRules()) {
        if (mapRule.equals(rule)) {
          return categoryMap.getName();
        }
      }
    }

    //Code should not reach this point if the configuration is not changed.
    System.out.println("FATAL ERROR: Category matching rule " + rule + " not found.");
    System.exit(-1);
    return null;
  }

  private List<String> getRulesForCategory(String category) {
    for (CategoryMap categoryMap : categoryRuleMap.getCategories()) {
      if (categoryMap.getName().equals(category)) {
        return categoryMap.getRules();
      }
    }

    //Code should not reach this point if the configuration is not changed.
    System.out.println("FATAL ERROR: Cannot file Category " + category + " in plugin configuration.");
    System.exit(-1);
    return null;
  }

  private List<String> getRulesForCategories(ArrayList<String> categories) {
    ArrayList<String> categoryRules = new ArrayList<String>();

    for (String category : categories) {
      categoryRules.addAll(getRulesForCategory(category));
    }

    return categoryRules;
  }

  public JlintFilter fromXml(String xml) {
    //Read xml config - which is in categories format
    //return List<ActiveRule>
    ArrayList<String> categories = JlintXmlConfigReader.readConfiguration(xml);
    List<String> rules = getRulesForCategories(categories);

    //create JlintFilter xml file so that it can be easily converted
    //to JlintFilter objects
    StringBuilder jlintFilterXml = new StringBuilder();
    jlintFilterXml.append("<JlintFilter>\n");
    for (String rule : rules) {
      jlintFilterXml.append("   <Match>\n      <Bug pattern=\"");
      jlintFilterXml.append(rule);
      jlintFilterXml.append("\"/>\n   </Match>\n");
    }
    jlintFilterXml.append("</JlintFilter>\n");

    //create JlintFilter object and return it
    JlintFilter filter = JlintFilter.fromXml(jlintFilterXml.toString());

    return filter;
  }
}
