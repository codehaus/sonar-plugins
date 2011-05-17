/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SQLi
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

package com.echosource.ada.rules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sonar.api.platform.ServerFileSystem;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.rules.XMLRuleParser;

import com.echosource.ada.Ada;

/**
 * The Class AdaRuleRepository handles ruleset and profile import and export
 */
public final class AdaRuleRepository extends RuleRepository {

  public static final String REPOSITORY_KEY = "ada-rule-repository";
  public static final String REPOSITORY_NAME = "Ada rules";
  /**
   * for user extensions
   */
  private ServerFileSystem fileSystem;

  /**
   * @param fileSystem
   */
  public AdaRuleRepository(ServerFileSystem fileSystem) {
    super(REPOSITORY_KEY, Ada.LANGUAGE_KEY);
    setName(REPOSITORY_NAME);
    this.fileSystem = fileSystem;
  }

  /**
   * @see org.sonar.api.rules.RuleRepository#createRules()
   */
  @Override
  public List<Rule> createRules() {
    List<Rule> rules = new ArrayList<Rule>();
    rules.addAll(XMLRuleParser.parseXML(getClass().getResourceAsStream("/com/hashcode/ada/rules.xml")));
    // Gives the ability for the user to extends system rules withs its own rules.
    // user rules must be set in SONAR_HOME/extensions/rules/REPOSITORY_KEY/*.xml
    for (File userExtensionXml : fileSystem.getExtensions(REPOSITORY_KEY, "xml")) {
      rules.addAll(XMLRuleParser.parseXML(userExtensionXml));
    }
    return rules;
  }

}