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
package org.sonar.plugins.secrules;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.configuration.Configuration;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulesManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

public class RulesParser {

  private List<Rule> rulesList;

  protected RulesParser(Configuration configuration, RulesManager rulesManager) {
    rulesList = new ArrayList<Rule>();

    String rawList = configuration.getString(SecurityRulesPlugin.SEC_RULES, SecurityRulesPlugin.SEC_RULES_DEFAULT);
    String[] tokens = StringUtils.split(rawList, ","); 

    for (String token : tokens){
      String[] s = StringUtils.split(token, ":");
      Rule rule = rulesManager.getPluginRule(s[0], s[1]);
      rulesList.add(rule);
    }
  }

  protected List<Rule> getRulesList() {
    return rulesList;
  }

}
