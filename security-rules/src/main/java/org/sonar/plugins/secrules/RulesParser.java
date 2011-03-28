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

package org.sonar.plugins.secrules;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.SonarException;

import java.util.ArrayList;
import java.util.List;

public final class RulesParser {

  private List<Rule> rulesList;

  protected RulesParser(Configuration configuration, RuleFinder ruleFinder) {
    rulesList = new ArrayList<Rule>();

    String[] tokens = configuration.getStringArray(SecurityRulesPlugin.SEC_RULES);

    if (ArrayUtils.isEmpty(tokens)) {
      tokens = StringUtils.split(SecurityRulesPlugin.SEC_RULES_DEFAULT, ",");
    }

    for (String token : tokens) {
      String[] s = StringUtils.split(token, ":");
      if (s.length != 2) {
        throw new SonarException("Parameter of Security Rules Plugin is incorrectly set, should be pluginKey1:ruleKey1,pluginKey2:ruleKey2...");
      }
      Rule rule = ruleFinder.findByKey(s[0], s[1]);
      if (rule == null) {
        throw new SonarException("Wrong plugin key (" + s[0] + ") or rule key (" + s[1] + ")");
      }
      rulesList.add(rule);
    }
  }

  protected List<Rule> getRulesList() {
    return rulesList;
  }

}
