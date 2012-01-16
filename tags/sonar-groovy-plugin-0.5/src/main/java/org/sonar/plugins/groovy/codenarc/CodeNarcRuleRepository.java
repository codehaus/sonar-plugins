/*
 * Sonar Groovy Plugin
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

package org.sonar.plugins.groovy.codenarc;

import com.google.common.collect.Lists;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.rules.XMLRuleParser;
import org.sonar.plugins.groovy.foundation.Groovy;

import java.util.List;

public class CodeNarcRuleRepository extends RuleRepository {

  private XMLRuleParser xmlRuleParser;

  public CodeNarcRuleRepository(XMLRuleParser xmlRuleParser) {
    super(CodeNarcConstants.REPOSITORY_KEY, Groovy.KEY);
    setName(CodeNarcConstants.REPOSITORY_NAME);
    this.xmlRuleParser = xmlRuleParser;
  }

  @Override
  public List<Rule> createRules() {
    List<Rule> rules = Lists.newArrayList();
    rules.addAll(xmlRuleParser.parse(getClass().getResourceAsStream("/org/sonar/plugins/groovy/rules.xml")));
    return rules;
  }

}
