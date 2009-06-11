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
package org.sonar.plugins.taglist;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.sonar.commons.resources.Measure;
import org.sonar.commons.rules.ActiveRule;
import org.sonar.commons.rules.Rule;
import org.sonar.commons.rules.RuleFailureLevel;
import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.matchers.IsJavaClass;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.rules.RulesManager;

public class TaglistViolationsXmlParserTest {

  private TaglistViolationsXmlParser parser = null;
  private ProjectContext context;

  @Before
  public void setUp() throws Exception {
    context = mock(ProjectContext.class);
    RulesManager rulesManager = mock(RulesManager.class);
    RulesProfile rulesProfile = mock(RulesProfile.class);


    when(rulesManager.getPluginRule(eq(TaglistPlugin.KEY), (String) anyObject())).thenReturn(new Rule());
    when(rulesProfile.getActiveRule(eq(TaglistPlugin.KEY), eq("TODO"))).thenReturn(new ActiveRule(null, null, RuleFailureLevel.WARNING));
    when(rulesProfile.getActiveRule(eq(TaglistPlugin.KEY), eq("FIXME"))).thenReturn(new ActiveRule(null, null, RuleFailureLevel.ERROR));
    when(rulesProfile.getActiveRule(eq(TaglistPlugin.KEY), eq("@todo"))).thenReturn(new ActiveRule(null, null, RuleFailureLevel.ERROR));
    when(rulesProfile.getActiveRule(eq(TaglistPlugin.KEY), eq("@fixme"))).thenReturn(new ActiveRule(null, null, RuleFailureLevel.ERROR));

    parser = new TaglistViolationsXmlParser(context, rulesManager, rulesProfile);
  }

  @Test
  public void testPopulateTaglistViolations() throws Exception {
    File xmlFile = new File(getClass().getResource("/org/sonar/plugins/taglist/TaglistViolationsXmlParserTest/taglist.xml").toURI());
    parser.populateTaglistViolation(xmlFile);

    
    verify(context, times(1)).addMeasure(argThat(new IsJavaClass("ClassOnDefaultPackage")), eq(TaglistMetrics.OPTIONAL_TAGS), eq(2d));
    
    verify(context, times(1)).addMeasure(argThat(new IsJavaClass("org.sonar.plugins.taglist.test.ClassWithTags")), eq(TaglistMetrics.MANDATORY_TAGS), eq(2d));
    verify(context, times(1)).addMeasure(argThat(new IsJavaClass("org.sonar.plugins.taglist.test.ClassWithTags")), eq(TaglistMetrics.OPTIONAL_TAGS), eq(2d));
    verify(context, times(1)).addMeasure(argThat(new IsJavaClass("org.sonar.plugins.taglist.test.ClassWithTags")), eq(TaglistMetrics.TAGS), eq(4d));

    verify(context, times(1)).addMeasure(argThat(new IsJavaClass("org.sonar.plugins.taglist.test.IInterfaceWithTags")), eq(TaglistMetrics.OPTIONAL_TAGS), eq(0d));
    verify(context, times(1)).addMeasure(argThat(new IsJavaClass("org.sonar.plugins.taglist.test.IInterfaceWithTags")), eq(TaglistMetrics.MANDATORY_TAGS), eq(2d));
    verify(context, times(1)).addMeasure(argThat(new IsJavaClass("org.sonar.plugins.taglist.test.IInterfaceWithTags")), eq(TaglistMetrics.TAGS), eq(2d));

    verify(context, times(1)).addMeasure(argThat(new BaseMatcher<Measure>() {

      public boolean matches(Object arg0) {
        Measure m = (Measure)arg0;
        return m.getMetric().equals(TaglistMetrics.TAGS_DISTRIBUTION) && m.getData().equals("@fixme=1;@todo=2;FIXME=1;TODO=4");
      }

      public void describeTo(Description arg0) {
      }}));

  }

}
