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

package org.sonar.plugins.emma;

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.resources.Project;
import org.sonar.api.test.MavenTestUtils;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmmaMavenPluginHandlerTest {

  private EmmaMavenPluginHandler handler;

  @Before
  public void before() {
    handler = new EmmaMavenPluginHandler();
  }

  @Test
  public void testMavenPluginDefinition() {
    assertThat(handler.getGroupId()).isEqualTo("org.codehaus.mojo");
    assertThat(handler.getArtifactId()).isEqualTo("emma-maven-plugin");
    assertThat(handler.getVersion()).isEqualTo("1.0-alpha-3");
    assertThat(handler.isFixedVersion()).isTrue();
    assertThat(handler.getGoals()).containsOnly("emma");
  }

  @Test
  public void enableXmlFormat() {
    Project project = MavenTestUtils.loadProjectFromPom(getClass(), "pom.xml");
    MavenPlugin plugin = new MavenPlugin(EmmaMavenPluginHandler.GROUP_ID, EmmaMavenPluginHandler.ARTIFACT_ID, "1.0-alpha-1");
    handler.configure(project, plugin);

    assertThat(plugin.getParameter("format")).isEqualTo("xml");
  }

  @Test
  public void shouldOverrideExistingConfiguration() {
    Project project = MavenTestUtils.loadProjectFromPom(getClass(), "Emma-pom.xml");
    MavenPlugin plugin = MavenPlugin.getPlugin(project.getPom(), EmmaMavenPluginHandler.GROUP_ID, EmmaMavenPluginHandler.ARTIFACT_ID);
    handler.configure(project, plugin);

    assertThat(plugin.getParameter("format")).isEqualTo("xml");
    assertThat(plugin.getParameter("foo")).isEqualTo("bar");
  }

  @Test
  public void testConfigurePluginWithFilterExclusions() {
    MavenProject pom = MavenTestUtils.loadPom(getClass(), "Emma-pom.xml");
    MavenPlugin plugin = MavenPlugin.getPlugin(pom, EmmaMavenPluginHandler.GROUP_ID, EmmaMavenPluginHandler.ARTIFACT_ID);

    Project project = mock(Project.class);
    when(project.getExclusionPatterns()).thenReturn(new String[]{"/com/foo**/bar/Ba*.java", "org.mypackage.MyClass"});

    handler.configure(project, plugin);

    assertThat(plugin.getParameters("filters/filter")).containsOnly("-com.foo*.bar.Ba*", "-org.mypackage.MyClass");
  }
}
