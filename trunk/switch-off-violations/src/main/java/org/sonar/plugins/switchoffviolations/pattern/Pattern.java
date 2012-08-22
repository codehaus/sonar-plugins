/*
 * Sonar Switch Off Violations Plugin
 * Copyright (C) 2011 SonarSource
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

package org.sonar.plugins.switchoffviolations.pattern;

import com.google.common.collect.Sets;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.WildcardPattern;

import java.util.Set;

public class Pattern {

  private WildcardPattern resourcePattern;
  private WildcardPattern rulePattern;
  private Set<Integer> lines = Sets.newLinkedHashSet();
  private Set<LineRange> lineRanges = Sets.newLinkedHashSet();
  private String regexp1;

  private String regexp2;
  private boolean checkLines = true;

  public Pattern() {
  }

  public Pattern(String resourcePattern, String rulePattern) {
    this.resourcePattern = WildcardPattern.create(resourcePattern);
    this.rulePattern = WildcardPattern.create(rulePattern);
  }

  public Pattern(String resourcePattern, String rulePattern, Set<LineRange> lineRanges) {
    this(resourcePattern, rulePattern);
    this.lineRanges = lineRanges;
  }

  public WildcardPattern getResourcePattern() {
    return resourcePattern;
  }

  public WildcardPattern getRulePattern() {
    return rulePattern;
  }

  public String getRegexp1() {
    return regexp1;
  }

  public String getRegexp2() {
    return regexp2;
  }

  Pattern addLineRange(int fromLineId, int toLineId) {
    lineRanges.add(new LineRange(fromLineId, toLineId));
    return this;
  }

  Pattern addLine(int lineId) {
    lines.add(lineId);
    return this;
  }

  boolean isCheckLines() {
    return checkLines;
  }

  Pattern setCheckLines(boolean b) {
    this.checkLines = b;
    return this;
  }

  Pattern setRegexp1(String regexp1) {
    this.regexp1 = regexp1;
    return this;
  }

  Pattern setRegexp2(String regexp2) {
    this.regexp2 = regexp2;
    return this;
  }

  Set<Integer> getAllLines() {
    Set<Integer> allLines = Sets.newLinkedHashSet(lines);
    for (LineRange lineRange : lineRanges) {
      allLines.addAll(lineRange.toLines());
    }
    return allLines;
  }

  public boolean match(Violation violation) {
    boolean match = matchResource(violation.getResource()) && matchRule(violation.getRule());
    if (match && checkLines && violation.getLineId() != null) {
      match = matchLine(violation.getLineId());
    }
    return match;
  }

  boolean matchLine(int lineId) {
    boolean match = lines.contains(lineId);
    if (!match) {
      for (LineRange range : lineRanges) {
        if (range.in(lineId)) {
          return true;
        }
      }
    }
    return match;
  }

  boolean matchRule(Rule rule) {
    if (rule != null) {
      String key = new StringBuilder().append(rule.getRepositoryKey()).append(':').append(rule.getKey()).toString();
      return rulePattern.match(key);
    }
    return false;
  }

  boolean matchResource(Resource<?> resource) {
    return resource != null && resource.getKey() != null && resourcePattern.match(resource.getKey());
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
