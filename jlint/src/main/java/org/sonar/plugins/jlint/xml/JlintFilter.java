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

package org.sonar.plugins.jlint.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RulePriorityMapper;
import org.sonar.plugins.jlint.JlintPlugin;
import org.sonar.plugins.jlint.JlintRulePriorityMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

  public Map<String, RulePriority> getPatternLevels(RulePriorityMapper priorityMapper) {
    BugInfoSplitter splitter = new BugInfoSplitter() {
      public String getSeparator() {
        return PATTERN_SEPARATOR;
      }

      public String getVar(Bug bug) {
        return bug.getPattern();
      }
    };
    return processMatches(priorityMapper, splitter);
  }

  public Map<String, RulePriority> getCodeLevels(RulePriorityMapper priorityMapper) {
    BugInfoSplitter splitter = new BugInfoSplitter() {
      public String getSeparator() {
        return CODE_SEPARATOR;
      }

      public String getVar(Bug bug) {
        return bug.getCode();
      }
    };
    return processMatches(priorityMapper, splitter);
  }

  public Map<String, RulePriority> getCategoryLevels(RulePriorityMapper priorityMapper) {
    BugInfoSplitter splitter = new BugInfoSplitter() {
      public String getSeparator() {
        return CATEGORY_SEPARATOR;
      }

      public String getVar(Bug bug) {
        return bug.getCategory();
      }
    };
    return processMatches(priorityMapper, splitter);
  }

  private RulePriority getRulePriority(Priority priority, RulePriorityMapper priorityMapper) {
    return (priority != null) ? priorityMapper.from(priority.getValue()) : null;
  }

  private Map<String, RulePriority> processMatches(RulePriorityMapper priorityMapper, BugInfoSplitter splitter) {
    Map<String, RulePriority> result = new HashMap<String, RulePriority>();
    for (Match child : getChildren()) {
      if (child.getOrs() != null) {
        for (OrFilter orFilter : child.getOrs()) {
          completeLevels(result, orFilter.getBugs(), child.getPriority(), priorityMapper, splitter);
        }
      }
      if (child.getBug() != null) {
        completeLevels(result, Arrays.asList(child.getBug()), child.getPriority(), priorityMapper, splitter);
      }
    }
    return result;
  }

  private void completeLevels(Map<String, RulePriority> result, List<Bug> bugs, Priority priority, RulePriorityMapper priorityMapper, BugInfoSplitter splitter) {
    if (bugs == null) {
      return;
    }
    RulePriority rulePriority = getRulePriority(priority, priorityMapper);
    for (Bug bug : bugs) {
      String varToSplit = splitter.getVar(bug);
      if (!StringUtils.isBlank(varToSplit)) {
        String[] splitted = StringUtils.split(varToSplit, splitter.getSeparator());
        for (String code : splitted) {
          mapRulePriority(result, rulePriority, code);
        }
      }
    }
  }

  private interface BugInfoSplitter {
    String getVar(Bug bug);

    String getSeparator();
  }

  private void mapRulePriority(Map<String, RulePriority> prioritiesByRule, RulePriority priority, String key) {
    if (prioritiesByRule.containsKey(key)) {
      if (prioritiesByRule.get(key).compareTo(priority) < 0) {
        prioritiesByRule.put(key, priority);
      }
    } else {
      prioritiesByRule.put(key, priority);
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

  public static JlintFilter fromActiveRules(List<ActiveRule> activeRules, JlintRulePriorityMapper mapper) {
    JlintFilter root = new JlintFilter();
    for (ActiveRule activeRule : activeRules) {
      if (JlintPlugin.KEY.equals(activeRule.getPluginName())) {
        Match child = createChild(activeRule, mapper);
        root.addMatch(child);
      }
    }
    return root;
  }

  private static Match createChild(ActiveRule activeRule, JlintRulePriorityMapper mapper) {
    Match child = new Match();
    child.setBug(new Bug(activeRule.getConfigKey()));
    return child;
  }

}
