package org.sonar.plugins.secrules;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertThat;
import org.sonar.api.rules.*;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.measures.CountDistributionBuilder;
import org.apache.commons.configuration.Configuration;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;
import org.mockito.stubbing.Answer;
import org.mockito.invocation.InvocationOnMock;
import static org.hamcrest.CoreMatchers.is;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import com.google.common.collect.Lists;
import static junit.framework.Assert.assertNull;

public class SecurityRulesDecoratorTest {
  private RulesManager rulesManager;
  private Configuration configuration;
  private RulesProfile rulesProfile;
  private SecurityRulesDecorator decorator;
  private Map<RulePriority, Integer> distribution;

  @Before
  public void init() {
    rulesManager = mock(RulesManager.class);

    when(rulesManager.getPluginRule(anyString(), anyString())).thenAnswer(new Answer<Rule>() {
      public Rule answer(InvocationOnMock invocationOnMock) throws Throwable {
        return new Rule((String) invocationOnMock.getArguments()[0], (String) invocationOnMock.getArguments()[1]);
      }
    });

    configuration = mock(Configuration.class);
    when(configuration.getString("sonar.core.rule.weight", "INFO=0;MINOR=1;MAJOR=3;CRITICAL=5;BLOCKER=10")).
      thenReturn("INFO=0;MINOR=1;MAJOR=3;CRITICAL=5;BLOCKER=10");

    decorator = new SecurityRulesDecorator(rulesManager, rulesProfile, configuration);

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
    violations.add(new Violation(new Rule("foo","bar")));

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

    rulesProfile = new RulesProfile();

    List<ActiveRule> list = Lists.newArrayList(
      new ActiveRule(rulesProfile, new Rule("findbugs", "DMI_CONSTANT_DB_PASSWORD"), RulePriority.BLOCKER),
      new ActiveRule(rulesProfile, new Rule("findbugs", "DMI_EMPTY_DB_PASSWORD"), RulePriority.BLOCKER),
      new ActiveRule(rulesProfile, new Rule("findbugs", "EI_EXPOSE_REP"), RulePriority.BLOCKER)
    );

    rulesProfile.setActiveRules(list);

    SecurityRulesDecorator decorator = new SecurityRulesDecorator(rulesManager, rulesProfile, configuration);

    assertThat(decorator.getUsedRules(), is(3));
  }
}
