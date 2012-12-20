/*
 * Sonar Emma plugin
 * Copyright (C) 2009 SonarSource
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
package org.sonar.plugins.emma;

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.test.MavenTestUtils;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmmaMavenInitializerTest {

  Project project;
  EmmaMavenInitializer initializer;
  EmmaSettings settings;

  @Before
  public void init() {
    ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
    when(fileSystem.getBuildDir()).thenReturn(new File("target"));
    project = mock(Project.class);
    when(project.getAnalysisType()).thenReturn(Project.AnalysisType.DYNAMIC);
    when(project.getFileSystem()).thenReturn(fileSystem);
    settings = mock(EmmaSettings.class);
    when(settings.isEnabled(project)).thenReturn(true);
    initializer = new EmmaMavenInitializer(new EmmaMavenPluginHandler(), settings, new MavenProject());
  }

  @Test
  public void should_execute_maven_plugin() {
    assertThat(initializer.getMavenPluginHandler(project).getArtifactId()).isEqualTo("emma-maven-plugin");
  }

  @Test
  public void should_not_override_existing_report_path() {
    when(settings.getReportPath()).thenReturn("path/to/report");
    initializer.execute(project);
    verify(settings, never()).setReportPath(anyString());
  }

  @Test
  public void should_use_report_path_declared_in_pom() throws Exception {
    MavenProject pom = MavenTestUtils.loadPom("/org/sonar/plugins/emma/EmmaMavenInitializerTest/shouldGetReportPathFromPom/pom.xml");
    initializer = new EmmaMavenInitializer(new EmmaMavenPluginHandler(), settings, pom);
    initializer.execute(project);
    verify(settings).setReportPath("overridden/dir");
  }

  @Test
  public void shouldSetDefaultReportPath() {
    ProjectFileSystem pfs = mock(ProjectFileSystem.class);
    when(pfs.getBuildDir()).thenReturn(new File("buildDir"));
    when(project.getFileSystem()).thenReturn(pfs);
    initializer.execute(project);
    verify(settings).setReportPath("buildDir");
  }

}
