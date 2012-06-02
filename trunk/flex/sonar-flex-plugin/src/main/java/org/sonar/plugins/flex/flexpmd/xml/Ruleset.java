/*
 * Sonar Flex Plugin
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

package org.sonar.plugins.flex.flexpmd.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("ruleset")
public class Ruleset {

  private String description;

  @XStreamImplicit
  private List<FlexRule> flexRules = new ArrayList<FlexRule>();

  @XStreamOmitField
  @XStreamAlias(value = "exclude-pattern")
  private String excludePattern;

  @XStreamOmitField
  @XStreamAlias(value = "include-pattern")
  private String includePattern;

  public Ruleset() {
  }

  public Ruleset(String description) {
    this.description = description;
  }

  public List<FlexRule> getRules() {
    return flexRules;
  }

  public void setRules(List<FlexRule> flexRules) {
    this.flexRules = flexRules;
  }

  public void addRule(FlexRule flexRule) {
    flexRules.add(flexRule);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getExcludePattern() {
    return excludePattern;
  }

  public void setExcludePattern(String excludePattern) {
    this.excludePattern = excludePattern;
  }

  public String getIncludePattern() {
    return includePattern;
  }

  public void setIncludePattern(String includePattern) {
    this.includePattern = includePattern;
  }

}
