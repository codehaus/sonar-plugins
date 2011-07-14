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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.Configuration;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.test.MavenTestUtils;

import java.io.File;

public class EmmaMavenInitializerTest {

  private Project project;
  private EmmaMavenInitializer initializer;

  @Before
  public void setUp() {
    project = mock(Project.class);
    initializer = new EmmaMavenInitializer(new EmmaMavenPluginHandler());
  }

  @Test
  public void doNotExecuteMavenPluginIfReuseReports() {
    when(project.getAnalysisType()).thenReturn(Project.AnalysisType.REUSE_REPORTS);
    assertThat(initializer.getMavenPluginHandler(project), nullValue());
  }

  @Test
  public void doNotExecuteMavenPluginIfStaticAnalysis() {
    when(project.getAnalysisType()).thenReturn(Project.AnalysisType.STATIC);
    assertThat(initializer.getMavenPluginHandler(project), nullValue());
  }

  @Test
  public void executeMavenPluginIfDynamicAnalysis() {
    when(project.getAnalysisType()).thenReturn(Project.AnalysisType.DYNAMIC);
    assertThat(initializer.getMavenPluginHandler(project), not(nullValue()));
    assertThat(initializer.getMavenPluginHandler(project).getArtifactId(), is("emma-maven-plugin"));
  }

  @Test
  public void doNotSetReportPathIfAlreadyConfigured() {
    Configuration configuration = mock(Configuration.class);
    when(configuration.containsKey(EmmaPlugin.REPORT_PATH_PROPERTY)).thenReturn(true);
    when(project.getConfiguration()).thenReturn(configuration);
    initializer.execute(project);
    verify(configuration, never()).setProperty(eq(EmmaPlugin.REPORT_PATH_PROPERTY), anyString());
  }

  @Test
  public void shouldSetReportPathFromPom() throws Exception {
    Configuration configuration = mock(Configuration.class);
    when(project.getConfiguration()).thenReturn(configuration);
    MavenProject pom = MavenTestUtils.loadPom("/org/sonar/plugins/emma/EmmaSensorTest/shouldGetReportPathFromPom/pom.xml");
    when(project.getPom()).thenReturn(pom);
    initializer.execute(project);
    verify(configuration).setProperty(eq(EmmaPlugin.REPORT_PATH_PROPERTY), eq("overridden/dir"));
  }

  @Test
  public void shouldSetDefaultReportPath() {
    ProjectFileSystem pfs = mock(ProjectFileSystem.class);
    when(pfs.getBuildDir()).thenReturn(new File("buildDir"));
    Configuration configuration = mock(Configuration.class);
    when(project.getConfiguration()).thenReturn(configuration);
    when(project.getFileSystem()).thenReturn(pfs);
    initializer.execute(project);
    verify(configuration).setProperty(eq(EmmaPlugin.REPORT_PATH_PROPERTY), eq("buildDir"));
  }

}
