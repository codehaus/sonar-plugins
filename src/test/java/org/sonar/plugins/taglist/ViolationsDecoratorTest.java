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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.doubleThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.rules.Violation;
import org.sonar.api.test.IsMeasure;

public class ViolationsDecoratorTest {

  private ViolationsDecorator decorator;
  private RulesProfile rulesProfile = RulesProfile.create();
  private Rule rule1, rule2, rule3;
  private RuleFinder ruleFinder;
  private DecoratorContext context;
  private JavaFile javaFile;

  @Before
  public void setUp() {
    rulesProfile = RulesProfile.create();
    rule1 = createCheckstyleRule().setKey("key1");
    rule2 = createCheckstyleRule().setKey("key2");
    rule3 = createSquidRule();
    Rule inactiveRule = createCheckstyleRule().setKey("key3");
    rulesProfile.activateRule(rule1, RulePriority.BLOCKER).setParameter("format", "FIXME");
    rulesProfile.activateRule(rule2, RulePriority.MAJOR).setParameter("format", "TODO");
    rulesProfile.activateRule(rule3, RulePriority.INFO);

    ruleFinder = mock(RuleFinder.class);
    when(ruleFinder.findAll(argThat(any(RuleQuery.class)))).thenReturn(Arrays.asList(rule1, rule2, inactiveRule, rule3));

    context = mock(DecoratorContext.class);
    javaFile = new JavaFile("org.example", "HelloWorld");

    decorator = new ViolationsDecorator(rulesProfile, ruleFinder);
  }

  @Test
  public void dependedUpon() {
    assertThat(decorator.dependedUpon().size(), is(5));
  }

  @Test
  public void shouldExecuteOnlyOnJavaProject() {
    Project project = mock(Project.class);
    Language anotherLanguage = mock(Language.class);
    when(project.getLanguage()).thenReturn(Java.INSTANCE).thenReturn(anotherLanguage);

    assertThat(decorator.shouldExecuteOnProject(project), is(true));
    assertThat(decorator.shouldExecuteOnProject(project), is(false));
  }

  @Test
  public void dontDecoratePackage() {
    Resource resource = mock(Resource.class);
    when(resource.getQualifier()).thenReturn(Resource.QUALIFIER_PACKAGE);
    ViolationsDecorator spy = spy(decorator);

    spy.decorate(resource, context);

    verify(spy, never()).saveFileMeasures(eq(context), argThat(any(Collection.class)));
  }

  @Test
  public void shouldSaveMetrics() {
    Violation mandatory = Violation.create(rule1, null);
    Violation optional = Violation.create(rule2, null);
    Violation info = Violation.create(rule3, null);
    when(context.getViolations()).thenReturn(Arrays.asList(mandatory, optional, info));

    decorator.decorate(javaFile, context);

    verify(context, atLeastOnce()).getViolations();
    verify(context).saveMeasure(eq(TaglistMetrics.TAGS), doubleThat(equalTo(3.0)));
    verify(context).saveMeasure(eq(TaglistMetrics.MANDATORY_TAGS), doubleThat(equalTo(1.0)));
    verify(context).saveMeasure(eq(TaglistMetrics.OPTIONAL_TAGS), doubleThat(equalTo(2.0)));
    verify(context).saveMeasure(eq(TaglistMetrics.NOSONAR_TAGS), doubleThat(equalTo(1.0)));
    verify(context).saveMeasure(argThat(new IsMeasure(TaglistMetrics.TAGS_DISTRIBUTION, "FIXME=1;NOSONAR=1;TODO=1")));
    verifyNoMoreInteractions(context);
  }

  @Test
  public void shouldntSaveMetricsIfNoTags() {
    when(context.getViolations()).thenReturn(Collections.<Violation> emptyList());

    decorator.decorate(javaFile, context);

    verify(context, atLeastOnce()).getViolations();
    verifyNoMoreInteractions(context);
  }

  @Test
  public void ruleRriorities() {
    assertThat(ViolationsDecorator.isMandatory(RulePriority.BLOCKER), is(true));
    assertThat(ViolationsDecorator.isMandatory(RulePriority.CRITICAL), is(true));
    assertThat(ViolationsDecorator.isMandatory(RulePriority.MAJOR), is(false));
    assertThat(ViolationsDecorator.isMandatory(RulePriority.MINOR), is(false));
    assertThat(ViolationsDecorator.isMandatory(RulePriority.INFO), is(false));
  }

  private Rule createSquidRule() {
    Rule rule = Rule.create();
    rule.setRepositoryKey(CoreProperties.SQUID_PLUGIN);
    return rule;
  }

  private Rule createCheckstyleRule() {
    Rule rule = Rule.create();
    rule.setRepositoryKey(CoreProperties.CHECKSTYLE_PLUGIN);
    rule.createParameter("format").setDefaultValue("TODO:");
    return rule;
  }

}
