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

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.measures.CountDistributionBuilder;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecurityRulesDecoratorTest {
  private RuleFinder ruleFinder;
  private Configuration configuration;
  private RulesProfile rulesProfile;
  private SecurityRulesDecorator decorator;
  private Map<RulePriority, Integer> distribution;

  @Before
  public void init() {
    ruleFinder = mock(RuleFinder.class);

    when(ruleFinder.findByKey(anyString(), anyString())).thenAnswer(new Answer<Rule>() {
      public Rule answer(InvocationOnMock invocationOnMock) throws Throwable {
        String repositoryKey = (String) invocationOnMock.getArguments()[0];
        String ruleKey = (String) invocationOnMock.getArguments()[1];
        return Rule.create(repositoryKey, ruleKey, ruleKey);
      }
    });

    configuration = mock(Configuration.class);
    when(configuration.getString("sonar.core.rule.weight", "INFO=0;MINOR=1;MAJOR=3;CRITICAL=5;BLOCKER=10")).
        thenReturn("INFO=0;MINOR=1;MAJOR=3;CRITICAL=5;BLOCKER=10");

    decorator = new SecurityRulesDecorator(ruleFinder, rulesProfile, configuration);

    distribution = new HashMap<RulePriority, Integer>();
    distribution.put(RulePriority.BLOCKER, 7);
    distribution.put(RulePriority.MAJOR, 5);
    distribution.put(RulePriority.MINOR, 0);
  }

  @Test
  public void testCountViolationsForRule() {
    ActiveRule activeRule = new ActiveRule(rulesProfile, new Rule("findbugs", "DMI_CONSTANT_DB_PASSWORD"), RulePriority.MAJOR);
    List<Violation> violations = new ArrayList<Violation>();
    violations.add(new Violation(activeRule.getRule()));
    violations.add(new Violation(activeRule.getRule()));
    violations.add(new Violation(new Rule("foo", "bar")));

    decorator.countViolationsForRule(distribution, activeRule, violations);
    assertThat(distribution.get(RulePriority.MAJOR), is(7));
  }

  @Test
  public void testCountViolationForRule() {
    decorator.countViolationForRule(distribution, RulePriority.BLOCKER);
    decorator.countViolationForRule(distribution, RulePriority.BLOCKER);
    decorator.countViolationForRule(distribution, RulePriority.MINOR);
    decorator.countViolationForRule(distribution, RulePriority.INFO);

    assertThat(distribution.get(RulePriority.BLOCKER), is(9));
    assertNull(distribution.get(RulePriority.CRITICAL));
    assertThat(distribution.get(RulePriority.MAJOR), is(5));
    assertThat(distribution.get(RulePriority.MINOR), is(1));
    assertThat(distribution.get(RulePriority.INFO), is(1));
  }

  @Test
  public void testComputeWeightedViolations() {
    assertThat(decorator.computeWeightedViolations(distribution), is(10 * 7 + 5 * 3 + 0 * 1));
  }

  @Test
  public void testCountViolations() {
    assertThat(decorator.countViolations(distribution), is(7 + 5 + 0));
  }

  @Test
  public void TestCountDistributionBuilder() {

    CountDistributionBuilder countDistribution = new CountDistributionBuilder(SecurityRulesMetrics.SECURITY_VIOLATIONS_DISTRIBUTION);
    countDistribution.add(RulePriority.BLOCKER, 7);
    countDistribution.add(RulePriority.MAJOR, 3);
    countDistribution.add(RulePriority.MINOR, 0);

    assertThat(countDistribution.build(), is(decorator.computeCountDistribution(distribution).build()));
  }

  @Test
  public void testUsedRules() {
    rulesProfile = RulesProfile.create();
    rulesProfile.activateRule(Rule.create("findbugs", "DMI_CONSTANT_DB_PASSWORD", ""), RulePriority.BLOCKER);
    rulesProfile.activateRule(Rule.create("findbugs", "DMI_EMPTY_DB_PASSWORD", ""), RulePriority.BLOCKER);
    rulesProfile.activateRule(Rule.create("findbugs", "EI_EXPOSE_REP", ""), RulePriority.BLOCKER);

    SecurityRulesDecorator decorator = new SecurityRulesDecorator(ruleFinder, rulesProfile, configuration);

    assertThat(decorator.getUsedRules(), is(3));
  }
}
