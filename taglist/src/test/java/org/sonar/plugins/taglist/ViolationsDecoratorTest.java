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

package org.sonar.plugins.taglist;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.doubleThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;
import org.sonar.api.test.IsMeasure;

public class ViolationsDecoratorTest {

  private ViolationsDecorator decorator;
  private RulesProfile rulesProfile = RulesProfile.create();
  private DecoratorContext context;

  @Before
  public void setUp() {
    rulesProfile = RulesProfile.create();
    decorator = new ViolationsDecorator(rulesProfile, null);
    context = mock(DecoratorContext.class);
  }

  @Test
  public void shouldSaveMetrics() {
    Rule rule1 = createCheckstyleRule().setKey("key1");
    Rule rule2 = createCheckstyleRule().setKey("key2");
    rulesProfile.activateRule(rule1, RulePriority.BLOCKER).setParameter("format", "FIXME");
    rulesProfile.activateRule(rule2, RulePriority.MAJOR).setParameter("format", "TODO");
    Violation mandatory = Violation.create(rule1, null);
    Violation optional = Violation.create(rule2, null);
    when(context.getViolations()).thenReturn(Arrays.asList(mandatory, optional));

    decorator.saveFileMeasures(context, Arrays.asList(rule1, rule2));

    verify(context, atLeastOnce()).getViolations();
    verify(context).saveMeasure(eq(TaglistMetrics.TAGS), doubleThat(equalTo(2.0)));
    verify(context).saveMeasure(eq(TaglistMetrics.MANDATORY_TAGS), doubleThat(equalTo(1.0)));
    verify(context).saveMeasure(eq(TaglistMetrics.OPTIONAL_TAGS), doubleThat(equalTo(1.0)));
    verify(context).saveMeasure(argThat(new IsMeasure(TaglistMetrics.TAGS_DISTRIBUTION, "FIXME=1;TODO=1")));
    verifyNoMoreInteractions(context);
  }

  @Test
  public void shouldntSaveMetricsIfNoTags() {
    Rule rule = createCheckstyleRule().setKey("key1");
    rulesProfile.activateRule(rule, RulePriority.BLOCKER).setParameter("format", "FIXME");
    when(context.getViolations()).thenReturn(Collections.<Violation> emptyList());

    decorator.saveFileMeasures(context, Arrays.asList(rule));

    verify(context, atLeastOnce()).getViolations();
    verifyNoMoreInteractions(context);
  }

  private Rule createCheckstyleRule() {
    Rule rule = Rule.create();
    rule.createParameter("format").setDefaultValue("TODO:");
    return rule;
  }

}
