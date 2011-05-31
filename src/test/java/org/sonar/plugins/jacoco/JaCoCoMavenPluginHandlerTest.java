/*
 * Sonar JaCoCo Plugin
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.jacoco;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.BaseConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenSurefireUtils;
import org.sonar.api.resources.Project;
import org.sonar.api.test.MavenTestUtils;

import java.io.File;

/**
 * @author Evgeny Mandrikov
 */
public class JaCoCoMavenPluginHandlerTest {

  private JacocoConfiguration configuration;
  private JaCoCoMavenPluginHandler handler;

  @Before
  public void setUp() throws Exception {
    JaCoCoAgentDownloader downloader = mock(JaCoCoAgentDownloader.class);
    when(downloader.getAgentJarFile()).thenReturn(new File("jacocoagent.jar"));
    Project project = mock(Project.class);
    when(project.getConfiguration()).thenReturn(new BaseConfiguration());
    configuration = spy(new JacocoConfiguration(project.getConfiguration(), downloader));

    handler = new JaCoCoMavenPluginHandler(configuration);
  }

  @Test
  public void testMavenPluginDefinition() {
    assertThat(handler.getGroupId(), is(MavenSurefireUtils.GROUP_ID));
    assertThat(handler.getArtifactId(), is(MavenSurefireUtils.ARTIFACT_ID));
    assertThat(handler.getVersion(), is(MavenSurefireUtils.VERSION));
    assertThat(handler.getGoals(), is(new String[] { "test" }));
    assertThat(handler.isFixedVersion(), is(false));
  }

  @Test
  public void testConfigureMavenPlugin() {
    Project project = MavenTestUtils.loadProjectFromPom(getClass(), "pom.xml");
    MavenPlugin plugin = new MavenPlugin(handler.getGroupId(), handler.getArtifactId(), handler.getVersion());

    handler.configure(project, plugin);

    verify(configuration).getJvmArgument();
    assertThat(plugin.getParameter("argLine"), is("-javaagent:jacocoagent.jar=destfile=target/jacoco.exec"));
  }

  @Test
  public void testReconfigureMavenPlugin() {
    Project project = MavenTestUtils.loadProjectFromPom(getClass(), "pom2.xml");
    MavenPlugin plugin = MavenPlugin.getPlugin(project.getPom(), handler.getGroupId(), handler.getArtifactId());

    handler.configure(project, plugin);

    verify(configuration).getJvmArgument();
    assertThat(plugin.getParameter("argLine"), is("-javaagent:jacocoagent.jar=destfile=target/jacoco.exec -esa"));
  }

}
