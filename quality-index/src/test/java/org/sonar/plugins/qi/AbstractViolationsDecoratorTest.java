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

package org.sonar.plugins.qi;

import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Metric;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractViolationsDecoratorTest {
  private AbstractViolationsDecorator decorator;
  private DecoratorContext context;
  private Settings settings;

  @Before
  public void init() {
    context = mock(DecoratorContext.class);
    settings = new Settings();
    decorator = new ViolationsDecoratorImpl(settings);
  }

  @Test
  public void testDependsUpon() {
    assertThat(decorator.dependsUpon().size(), is(1));
  }

  @Test
  public void testGetWeightsByPriority() {
    settings.setProperty("configKey", "MAJOR=3;BLOCKER=17;INFO=6");

    Map<RulePriority, Integer> weights = new HashMap<RulePriority, Integer>();
    weights.put(RulePriority.BLOCKER, 17);
    weights.put(RulePriority.INFO, 6);
    weights.put(RulePriority.MAJOR, 3);

    assertThat(decorator.getWeightsByPriority(), is(weights));
  }

  @Test
  public void testCountViolationsBySeverity() {
    createMultiSetViolations();
    Multiset<RulePriority> set = decorator.countViolationsBySeverity(context);
    assertThat(set.count(RulePriority.BLOCKER), is(2));
    assertThat(set.count(RulePriority.CRITICAL), is(0));
    assertThat(set.count(RulePriority.MAJOR), is(1));
    assertThat(set.count(RulePriority.INFO), is(1));
  }

  @Test
  public void getWeightedViolations() {
    settings.setProperty("configKey", "INFO=1;MINOR=1;MAJOR=3;CRITICAL=5;BLOCKER=10");
    createMultiSetViolations();
    Multiset<RulePriority> set = decorator.countViolationsBySeverity(context);
    Map<RulePriority, Integer> map = decorator.getWeightsByPriority();

    assertThat(decorator.getWeightedViolations(map, set, context), is(24.0));
  }

  private void createMultiSetViolations() {
    List<Violation> violations = Lists.newArrayList(
        Violation.create(Rule.create(CoreProperties.PMD_PLUGIN, "a"), null).setSeverity(RulePriority.BLOCKER),
        Violation.create(Rule.create(CoreProperties.PMD_PLUGIN, "b"), null).setSeverity(RulePriority.BLOCKER),
        Violation.create(Rule.create(CoreProperties.CHECKSTYLE_PLUGIN, "c"), null).setSeverity(RulePriority.BLOCKER),
        Violation.create(Rule.create("joe", "bloch"), null).setSeverity(RulePriority.BLOCKER),
        Violation.create(Rule.create(CoreProperties.PMD_PLUGIN, "e"), null).setSeverity(RulePriority.MAJOR),
        Violation.create(Rule.create(CoreProperties.PMD_PLUGIN, "hic"), null).setSeverity(RulePriority.INFO)
        );
    when(context.getViolations()).
        thenReturn(violations);
  }

  public class ViolationsDecoratorImpl extends AbstractViolationsDecorator {
    public ViolationsDecoratorImpl(Settings settings) {
      super(settings, null, null);
    }

    public String getConfigurationKey() {
      return "configKey";
    }

    public Metric getWeightedViolationMetricKey() {
      return null;
    }

    public String getPluginKey() {
      return CoreProperties.PMD_PLUGIN;
    }
  }
}
