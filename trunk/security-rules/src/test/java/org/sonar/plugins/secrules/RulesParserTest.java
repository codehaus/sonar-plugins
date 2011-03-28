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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;

import java.util.List;

public class RulesParserTest {

  @Test
  public void testDefaultConfig() {
    Configuration configuration = mock(Configuration.class);
    RuleFinder ruleFinder = mock(RuleFinder.class);

    when(configuration.getString(SecurityRulesPlugin.SEC_RULES, SecurityRulesPlugin.SEC_RULES_DEFAULT)).
        thenReturn(SecurityRulesPlugin.SEC_RULES_DEFAULT);

    when(ruleFinder.findByKey(anyString(), anyString())).thenAnswer(new Answer<Rule>() {
      public Rule answer(InvocationOnMock invocationOnMock) throws Throwable {
        String repositoryKey = (String) invocationOnMock.getArguments()[0];
        String ruleKey = (String) invocationOnMock.getArguments()[1];
        return Rule.create(repositoryKey, ruleKey, ruleKey);
      }
    });

    List<Rule> rulesList = new RulesParser(configuration, ruleFinder).getRulesList();
    assertThat(rulesList.size(), is(17));
  }

}
