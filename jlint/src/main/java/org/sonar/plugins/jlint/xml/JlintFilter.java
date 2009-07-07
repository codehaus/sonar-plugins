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
package org.sonar.plugins.jlint.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.IOUtils;
import org.sonar.commons.rules.RuleFailureLevel;
import org.sonar.commons.rules.ActiveRule;
import org.sonar.plugins.jlint.JlintPlugin;

import java.util.*;
import java.io.InputStream;
import java.io.IOException;

@XStreamAlias("JlintFilter")
public class JlintFilter {

  private static final String PATTERN_SEPARATOR = ",";
  private static final String CODE_SEPARATOR = ",";
  private static final String CATEGORY_SEPARATOR = ",";

  @XStreamImplicit
  private List<Match> matchs;

  public JlintFilter() {
    matchs = new ArrayList<Match>();
  }

  public String toXml() {
    XStream xstream = createXStream();
    return xstream.toXML(this);
  }

  public List<Match> getMatchs() {
    return matchs;
  }

  public List<Match> getChildren() {
    return matchs;
  }

  public void setMatchs(List<Match> matchs) {
    this.matchs = matchs;
  }

  public void addMatch(Match child) {
    matchs.add(child);
  }

  public Map<String, RuleFailureLevel> getPatternLevels() {
    Map<String, RuleFailureLevel> result = new HashMap<String, RuleFailureLevel>();

    for (Match child : getChildren()) {
      if (child.getOrs() != null) {
        for (OrFilter orFilter : child.getOrs()) {
          completePatternLevels(result, orFilter.getBugs(), child.getPriority());
        }
      }
      if (child.getBug() != null) {
        completePatternLevels(result, Arrays.asList(child.getBug()), child.getPriority());
      }
    }
    return result;
  }

  private void completePatternLevels(Map<String, RuleFailureLevel> result, List<Bug> bugs, Priority priority) {
    if (bugs == null) {
      return;
    }

    RuleFailureLevel level = RuleFailureLevel.ERROR;
    if (priority != null) {
      level = priority.toRuleFailureLevel();
    }

    for (Bug bug : bugs) {
      if (!StringUtils.isBlank(bug.getPattern())) {
        String[] patterns = StringUtils.split(bug.getPattern(), PATTERN_SEPARATOR);
        for (String pattern : patterns) {
          mapRuleFailureLevel(result, level, pattern);
        }
      }
    }
  }

  public Map<String, RuleFailureLevel> getCodeLevels() {
    Map<String, RuleFailureLevel> result = new HashMap<String, RuleFailureLevel>();

    for (Match child : getChildren()) {
      if (child.getOrs() != null) {
        for (OrFilter orFilter : child.getOrs()) {
          completeCodeLevels(result, orFilter.getBugs(), child.getPriority());
        }
      }
      if (child.getBug() != null) {
        completeCodeLevels(result, Arrays.asList(child.getBug()), child.getPriority());
      }
    }
    return result;
  }

  private void completeCodeLevels(Map<String, RuleFailureLevel> result, List<Bug> bugs, Priority priority) {
    if (bugs == null) {
      return;
    }

    RuleFailureLevel level = RuleFailureLevel.ERROR;
    if (priority != null) {
      level = priority.toRuleFailureLevel();
    }

    for (Bug bug : bugs) {
      if (!StringUtils.isBlank(bug.getCode())) {
        String[] codes = StringUtils.split(bug.getCode(), CODE_SEPARATOR);
        for (String code : codes) {
          mapRuleFailureLevel(result, level, code);
        }
      }
    }
  }

  public Map<String, RuleFailureLevel> getCategoryLevels() {
    Map<String, RuleFailureLevel> result = new HashMap<String, RuleFailureLevel>();

    for (Match child : getChildren()) {
      if (child.getOrs() != null) {
        for (OrFilter orFilter : child.getOrs()) {
          completeCategoryLevels(result, orFilter.getBugs(), child.getPriority());
        }
      }
      if (child.getBug() != null) {
        completeCategoryLevels(result, Arrays.asList(child.getBug()), child.getPriority());
      }
    }
    return result;
  }

  private void completeCategoryLevels(Map<String, RuleFailureLevel> result, List<Bug> bugs, Priority priority) {
    if (bugs == null) {
      return;
    }

    RuleFailureLevel level = RuleFailureLevel.ERROR;
    if (priority != null) {
      level = priority.toRuleFailureLevel();
    }

    for (Bug bug : bugs) {
      if (!StringUtils.isBlank(bug.getCategory())) {
        String[] categories = StringUtils.split(bug.getCategory(), CATEGORY_SEPARATOR);
        for (String category : categories) {
          mapRuleFailureLevel(result, level, category);
        }
      }
    }
  }


  private void mapRuleFailureLevel(Map<String, RuleFailureLevel> levelsByRule, RuleFailureLevel level, String key) {
    if (levelsByRule.containsKey(key)) {
      if (levelsByRule.get(key).compareTo(level) < 0) {
        levelsByRule.put(key, level);
      }
    } else {
      levelsByRule.put(key, level);
    }
  }

  public static XStream createXStream() {
    XStream xstream = new XStream();
    xstream.processAnnotations(JlintFilter.class);
    xstream.processAnnotations(Match.class);
    xstream.processAnnotations(Bug.class);
    xstream.processAnnotations(Priority.class);
    xstream.processAnnotations(ClassFilter.class);
    xstream.processAnnotations(PackageFilter.class);
    xstream.processAnnotations(MethodFilter.class);
    xstream.processAnnotations(FieldFilter.class);
    xstream.processAnnotations(LocalFilter.class);
    xstream.processAnnotations(OrFilter.class);
    return xstream;
  }

  public static JlintFilter fromXml(String xml) {
    try {
      XStream xStream = createXStream();
      InputStream inputStream = IOUtils.toInputStream(xml, "UTF-8");
      return (JlintFilter) xStream.fromXML(inputStream);

    } catch (IOException e) {
      throw new RuntimeException("can't read configuration file", e);
    }
  }

  public static JlintFilter fromActiveRules(List<ActiveRule> activeRules) {
    JlintFilter root = new JlintFilter();
    for (ActiveRule activeRule : activeRules) {
      if (JlintPlugin.KEY.equals(activeRule.getPluginName())) {
        Match child = createChild(activeRule);
        root.addMatch(child);
      }
    }
    return root;
  }

  private static Match createChild(ActiveRule activeRule) {
    Bug bug = new Bug(activeRule.getConfigKey());
    Match child = new Match();
    child.setBug(bug);
    return child;
  }

}
