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
package org.sonar.plugins.emma;

import org.apache.maven.project.MavenProject;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenUtils;
import org.sonar.api.resources.Project;
import org.sonar.api.test.MavenTestUtils;

public class EmmaMavenPluginHandlerTest {

  private EmmaMavenPluginHandler handler;

  @Before
  public void before() {
    handler = new EmmaMavenPluginHandler();
  }

  @Test
  public void enableXmlFormat() {
    Project project = MavenTestUtils.loadProjectFromPom(getClass(), "pom.xml");
    MavenPlugin plugin = new MavenPlugin(EmmaMavenPluginHandler.GROUP_ID, EmmaMavenPluginHandler.ARTIFACT_ID);
    handler.configure(project, plugin);

    assertThat(plugin.getConfigParameter("format"), is("xml"));
  }

  @Test
  public void shouldOverrideExistingConfiguration() {
    Project project = MavenTestUtils.loadProjectFromPom(getClass(), "Emma-pom.xml");
    MavenPlugin plugin = MavenUtils.getPlugin(project.getMavenProject(), EmmaMavenPluginHandler.GROUP_ID, EmmaMavenPluginHandler.ARTIFACT_ID);
    handler.configure(project, plugin);

    assertEquals("xml", plugin.getConfigParameter("format"));
    assertEquals("bar", plugin.getConfigParameter("foo"));
  }

  @Test
  public void testConfigurePluginWithFilterExclusions() {
    MavenProject pom = MavenTestUtils.loadPom(getClass(), "Emma-pom.xml");
    MavenPlugin plugin = MavenUtils.getPlugin(pom, EmmaMavenPluginHandler.GROUP_ID, EmmaMavenPluginHandler.ARTIFACT_ID);

    Project project = mock(Project.class);
    when(project.getExclusionPatterns()).thenReturn(new String[]{"/com/foo**/bar/Ba*.java"});

    handler.configure(project, plugin);

    assertEquals(1, plugin.getConfiguration().getParameters("filters/filter").length);
    assertThat(plugin.getConfiguration().getParameters("filters/filter"), is(new String[]{"com.foo*.bar.Ba*"}));
  }
}
