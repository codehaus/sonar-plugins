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
package org.sonar.plugins.jlint;

import org.sonar.commons.resources.Resource;
import org.sonar.commons.rules.ActiveRule;
import org.sonar.commons.rules.RuleFailureLevel;
import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.maven.AbstractViolationsXmlParser;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.rules.RulesManager;
import org.w3c.dom.Element;

class JlintViolationsXmlParser extends AbstractViolationsXmlParser {
  private RulesProfile activeProfile;

  JlintViolationsXmlParser(ProjectContext context, RulesProfile activeProfile, RulesManager rulesManager) {
    super(context, rulesManager);
    this.activeProfile = activeProfile;
  }

  @Override
  protected String xpathForResources() {
    return "/BugCollection/file";
  }

  @Override
  protected String elementNameForViolation() {
    return "BugInstance";
  }

  @Override
  protected Resource toResource(Element element) {
    return Java.newClass(element.getAttribute("classname"));
  }

  @Override
  protected String ruleKey(Element failure) {
    return failure.getAttribute("type");
  }

  @Override
  protected String keyForPlugin() {
    return JlintPlugin.KEY;
  }

  @Override
  protected RuleFailureLevel levelForViolation(Element violation) {
    String ruleKey = ruleKey(violation);
    ActiveRule activeRule = activeProfile.getActiveRule(keyForPlugin(), ruleKey);
    if (activeRule == null) {
      throw new IllegalStateException("Jlint rule '" + ruleKey + "' is not found in the Active ruleset for this quality profile. \nMost likely cause of this error is that all rules in the Jlint category (this rule belongs to) have not been completely enabled or disabled. Go to the quality profiles in Sonar and ensure that All rules from a category are either enabled or disabled.");
    }
    return activeRule.getLevel();
  }

  @Override
  protected String lineNumberForViolation(Element violation) {
    return violation.getAttribute("lineNumber");
  }

  @Override
  protected String messageFor(Element violation) {
    return violation.getAttribute("message");
  }
}
