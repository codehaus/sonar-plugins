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

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RulesManager;
import org.sonar.api.test.IsResource;

import java.io.File;

public class TaglistViolationsXmlParserTest {

  private TaglistViolationsXmlParser parser = null;
  private SensorContext context;
  private Project pom;

  @Before
  public void setUp() throws Exception {
    context = mock(SensorContext.class);
    pom = mock(Project.class);

    RulesManager rulesManager = mock(RulesManager.class);
    RulesProfile rulesProfile = mock(RulesProfile.class);


    when(rulesManager.getPluginRule(eq(TaglistPlugin.KEY), (String) anyObject())).thenReturn(new Rule());
    when(rulesProfile.getActiveRule(eq(TaglistPlugin.KEY), eq("TODO"))).thenReturn(new ActiveRule(null, null, RulePriority.MAJOR));
    when(rulesProfile.getActiveRule(eq(TaglistPlugin.KEY), eq("FIXME"))).thenReturn(new ActiveRule(null, null, RulePriority.CRITICAL));
    when(rulesProfile.getActiveRule(eq(TaglistPlugin.KEY), eq("@todo"))).thenReturn(new ActiveRule(null, null, RulePriority.BLOCKER));
    when(rulesProfile.getActiveRule(eq(TaglistPlugin.KEY), eq("@fixme"))).thenReturn(new ActiveRule(null, null, RulePriority.CRITICAL));

    when(pom.getPom()).thenReturn(new MavenProject());
    parser = new TaglistViolationsXmlParser(rulesManager, rulesProfile);
  }

  @Test
  public void testPopulateTaglistViolations() throws Exception {
    File xmlFile = new File(getClass().getResource("/org/sonar/plugins/taglist/TaglistViolationsXmlParserTest/taglist.xml").toURI());
    parser.populateTaglistViolation(context, pom, xmlFile);

    verify(context, never()).saveMeasure(argThat(new IsResource(Resource.SCOPE_FILE, Resource.QUALIFIER_CLASS, "[default].ClassOnDefaultPackage")), eq(TaglistMetrics.MANDATORY_TAGS), anyDouble());
    verify(context).saveMeasure(argThat(new IsResource(Resource.SCOPE_FILE, Resource.QUALIFIER_CLASS, "[default].ClassOnDefaultPackage")), eq(TaglistMetrics.OPTIONAL_TAGS), eq(2d));
    verify(context).saveMeasure(argThat(new IsResource(Resource.SCOPE_FILE, Resource.QUALIFIER_CLASS, "[default].ClassOnDefaultPackage")), eq(TaglistMetrics.TAGS), eq(2d));


    verify(context).saveMeasure(argThat(new IsResource(Resource.SCOPE_FILE, Resource.QUALIFIER_CLASS, "org.sonar.plugins.taglist.test.ClassWithTags")), eq(TaglistMetrics.MANDATORY_TAGS), eq(2d));
    verify(context).saveMeasure(argThat(new IsResource(Resource.SCOPE_FILE, Resource.QUALIFIER_CLASS, "org.sonar.plugins.taglist.test.ClassWithTags")), eq(TaglistMetrics.OPTIONAL_TAGS), eq(2d));
    verify(context).saveMeasure(argThat(new IsResource(Resource.SCOPE_FILE, Resource.QUALIFIER_CLASS, "org.sonar.plugins.taglist.test.ClassWithTags")), eq(TaglistMetrics.TAGS), eq(4d));

    verify(context, never()).saveMeasure(argThat(new IsResource(Resource.SCOPE_FILE, Resource.QUALIFIER_CLASS, "org.sonar.plugins.taglist.test.IInterfaceWithTags")), eq(TaglistMetrics.OPTIONAL_TAGS), anyDouble());
    verify(context).saveMeasure(argThat(new IsResource(Resource.SCOPE_FILE, Resource.QUALIFIER_CLASS, "org.sonar.plugins.taglist.test.IInterfaceWithTags")), eq(TaglistMetrics.MANDATORY_TAGS), eq(2d));
    verify(context).saveMeasure(argThat(new IsResource(Resource.SCOPE_FILE, Resource.QUALIFIER_CLASS, "org.sonar.plugins.taglist.test.IInterfaceWithTags")), eq(TaglistMetrics.TAGS), eq(2d));
  }

}
