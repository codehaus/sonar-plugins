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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;

public class JacocoMavenInitializerTest {
  private JaCoCoMavenPluginHandler mavenPluginHandler;
  private JacocoMavenInitializer initializer;

  @Before
  public void setUp() {
    mavenPluginHandler = mock(JaCoCoMavenPluginHandler.class);
    initializer = new JacocoMavenInitializer(mavenPluginHandler);
  }

  @Test
  public void shouldDoNothing() {
    Project project = mockProject();
    initializer.execute(project);
    verifyNoMoreInteractions(project);
    verifyNoMoreInteractions(mavenPluginHandler);
  }

  @Test
  public void shouldExecuteMaven() {
    Project project = mockProject();
    when(project.getFileSystem().hasTestFiles(argThat(is(Java.INSTANCE)))).thenReturn(true);
    when(project.getAnalysisType()).thenReturn(Project.AnalysisType.DYNAMIC);

    assertThat(initializer.shouldExecuteOnProject(project), is(true));
    assertThat(initializer.getMavenPluginHandler(project), instanceOf(JaCoCoMavenPluginHandler.class));
  }

  @Test
  public void shouldNotExecuteMavenWhenReuseReports() {
    Project project = mockProject();
    when(project.getFileSystem().hasTestFiles(argThat(is(Java.INSTANCE)))).thenReturn(true);
    when(project.getAnalysisType()).thenReturn(Project.AnalysisType.REUSE_REPORTS);

    assertThat(initializer.shouldExecuteOnProject(project), is(false));
  }

  @Test
  public void shouldNotExecuteMavenWhenNoTests() {
    Project project = mockProject();
    when(project.getFileSystem().hasTestFiles(argThat(is(Java.INSTANCE)))).thenReturn(false);
    when(project.getAnalysisType()).thenReturn(Project.AnalysisType.DYNAMIC);

    assertThat(initializer.shouldExecuteOnProject(project), is(false));
  }

  private Project mockProject() {
    Project project = mock(Project.class);
    ProjectFileSystem projectFileSystem = mock(ProjectFileSystem.class);
    when(project.getFileSystem()).thenReturn(projectFileSystem);
    return project;
  }
}
