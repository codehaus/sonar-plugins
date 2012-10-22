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
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.test.MavenTestUtils;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmmaMavenInitializerTest {

  private Project project;
  private EmmaMavenInitializer initializer;
  private Settings settings;
  private MavenProject mavenProject;

  @Before
  public void setUp() {
    project = mock(Project.class);
    settings = new Settings();
    mavenProject = new MavenProject();
    initializer = new EmmaMavenInitializer(new EmmaMavenPluginHandler(), settings, mavenProject);
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
    settings.setProperty(EmmaPlugin.REPORT_PATH_PROPERTY, "foo");
    initializer.execute(project);
    assertThat(settings.getString(EmmaPlugin.REPORT_PATH_PROPERTY), is("foo"));
  }

  @Test
  public void shouldSetReportPathFromPom() throws Exception {
    mavenProject = MavenTestUtils.loadPom("/org/sonar/plugins/emma/EmmaSensorTest/shouldGetReportPathFromPom/pom.xml");
    initializer = new EmmaMavenInitializer(new EmmaMavenPluginHandler(), settings, mavenProject);
    initializer.execute(project);
    assertThat(settings.getString(EmmaPlugin.REPORT_PATH_PROPERTY), is("overridden/dir"));
  }

  @Test
  public void shouldSetDefaultReportPath() {
    ProjectFileSystem pfs = mock(ProjectFileSystem.class);
    when(pfs.getBuildDir()).thenReturn(new File("buildDir"));
    when(project.getFileSystem()).thenReturn(pfs);
    initializer.execute(project);
    assertThat(settings.getString(EmmaPlugin.REPORT_PATH_PROPERTY), is("buildDir"));
  }

}
