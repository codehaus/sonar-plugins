/*
 * Sonar C# Plugin :: Core
 * Copyright (C) 2010 Jose Chillan, Alexandre Victoor and SonarSource
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

package org.sonar.plugins.csharp.api.sensor;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sonar.plugins.csharp.api.CSharpConstants.ASSEMBLIES_TO_SCAN_KEY;
import static org.sonar.plugins.csharp.api.CSharpConstants.BUILD_CONFIGURATIONS_DEFVALUE;
import static org.sonar.plugins.csharp.api.CSharpConstants.BUILD_CONFIGURATIONS_KEY;

import java.io.File;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.dotnet.tools.commons.visualstudio.VisualStudioProject;
import org.sonar.dotnet.tools.commons.visualstudio.VisualStudioSolution;
import org.sonar.plugins.csharp.api.CSharpConfiguration;
import org.sonar.plugins.csharp.api.MicrosoftWindowsEnvironment;

import com.google.common.collect.Lists;

public class AbstractRegularCSharpSensorTest {

  class FakeSensor extends AbstractRegularCSharpSensor {

    public FakeSensor(MicrosoftWindowsEnvironment microsoftWindowsEnvironment) {
      super(configurationMock, microsoftWindowsEnvironment, "FakeTool", "");
    }

    @Override
    public void analyse(Project project, SensorContext context) {
    }
  }

  class FakeCilSensor extends AbstractRegularCSharpSensor {

    public FakeCilSensor() {
      super(configurationMock, microsoftWindowsEnvironment, "SomeEngine", "");
    }

    public FakeCilSensor(String executionMode) {
      super(configurationMock, microsoftWindowsEnvironment, "SomeEngine", executionMode);
    }

    @Override
    protected boolean isCilSensor() {
      return true;
    }

    @Override
    public void analyse(Project project, SensorContext context) {
    }
  }

  private AbstractCSharpSensor sensor;
  private AbstractCSharpSensor cilSensor;
  private MicrosoftWindowsEnvironment microsoftWindowsEnvironment;
  private VisualStudioProject vsProject1;
  private CSharpConfiguration configurationMock;

  @Before
  public void init() {
    vsProject1 = mock(VisualStudioProject.class);
    when(vsProject1.getName()).thenReturn("Project #1");
    VisualStudioProject vsProject2 = mock(VisualStudioProject.class);
    when(vsProject2.getName()).thenReturn("Project Test");
    when(vsProject2.isTest()).thenReturn(true);
    VisualStudioSolution solution = mock(VisualStudioSolution.class);
    when(solution.getProjects()).thenReturn(Lists.newArrayList(vsProject1, vsProject2));

    microsoftWindowsEnvironment = new MicrosoftWindowsEnvironment();
    microsoftWindowsEnvironment.setCurrentSolution(solution);
    
    configurationMock = mock(CSharpConfiguration.class);

    sensor = new FakeSensor(microsoftWindowsEnvironment);
    cilSensor = new FakeCilSensor();
    
  }

  @Test
  public void testShouldNotExecuteOnTestProject() {
    Project project = mock(Project.class);
    when(project.getName()).thenReturn("Project Test");
    when(project.getLanguageKey()).thenReturn("cs");
    assertFalse(sensor.shouldExecuteOnProject(project));
  }

  @Test
  public void testShouldExecuteOnNormalProject() {
    Project project = mock(Project.class);
    when(project.getName()).thenReturn("Project #1");
    when(project.getLanguageKey()).thenReturn("cs");
    assertTrue(sensor.shouldExecuteOnProject(project));
  }

  @Test
  public void testFromFile() throws Exception {
    ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
    when(fileSystem.getSourceDirs()).thenReturn(Lists.newArrayList(new File("toto")));
    Project project = mock(Project.class);
    when(project.getFileSystem()).thenReturn(fileSystem);

    assertThat(sensor.fromIOFile(new File("toto/tata/fake.cs"), project).getKey(), is("tata/fake.cs"));
  }
  
  @Test
  public void testShouldCilSensorNotExecuteOnTestProject() {
    Project project = mock(Project.class);
    when(project.getName()).thenReturn("Project Test");
    when(project.getLanguageKey()).thenReturn("cs");
    assertFalse(cilSensor.shouldExecuteOnProject(project));
  }

  @Test
  public void testShouldCilSensorNotExecuteOnRootProject() {
    Project project = mock(Project.class);
    when(project.isRoot()).thenReturn(true);
    assertFalse(cilSensor.shouldExecuteOnProject(project));
  }

  @Test
  public void testShouldCilSensorNotExecuteOnRootProjectOnReuseMode() {
    cilSensor = new FakeCilSensor(FakeSensor.MODE_REUSE_REPORT);
    Project project = mock(Project.class);
    when(project.isRoot()).thenReturn(true);
    assertFalse(cilSensor.shouldExecuteOnProject(project));
  }

  @Test
  public void testShouldCilSensorExecuteOnNormalProjectOnReuseMode() {
    cilSensor = new FakeCilSensor(FakeSensor.MODE_REUSE_REPORT);
    Project project = mock(Project.class);
    when(project.getName()).thenReturn("Project #1");
    when(project.getLanguageKey()).thenReturn("cs");
    when(configurationMock.getString(BUILD_CONFIGURATIONS_KEY,
        BUILD_CONFIGURATIONS_DEFVALUE)).thenReturn(BUILD_CONFIGURATIONS_DEFVALUE);
    assertTrue(cilSensor.shouldExecuteOnProject(project));
  }

  @Test
  public void testShouldCilSensorExecuteOnNormalProject() {
    Project project = mock(Project.class);
    when(project.getName()).thenReturn("Project #1");
    when(project.getLanguageKey()).thenReturn("cs");
    when(configurationMock.getString(BUILD_CONFIGURATIONS_KEY,
        BUILD_CONFIGURATIONS_DEFVALUE)).thenReturn(BUILD_CONFIGURATIONS_DEFVALUE);

    when(vsProject1.getGeneratedAssemblies(BUILD_CONFIGURATIONS_DEFVALUE)).thenReturn(Collections.singleton(new File("toto")));
    assertTrue(cilSensor.shouldExecuteOnProject(project));
  }

  @Test
  public void testShouldCilSensorNotExecuteOnNormalProjectWithoutAssemblies() {
    Project project = mock(Project.class);
    when(project.getName()).thenReturn("Project #1");
    when(project.getLanguageKey()).thenReturn("cs");
    when(configurationMock.getString(BUILD_CONFIGURATIONS_KEY,
        BUILD_CONFIGURATIONS_DEFVALUE)).thenReturn(BUILD_CONFIGURATIONS_DEFVALUE);

    assertFalse(cilSensor.shouldExecuteOnProject(project));
  }

  @Test
  public void testShouldCilSensorExecuteOnNormalProjectWithBadPattern() {
    Project project = mock(Project.class);
    when(project.getName()).thenReturn("Project #1");
    when(project.getLanguageKey()).thenReturn("cs");
    when(configurationMock.getString(BUILD_CONFIGURATIONS_KEY,
        BUILD_CONFIGURATIONS_DEFVALUE)).thenReturn(BUILD_CONFIGURATIONS_DEFVALUE);
    when(configurationMock.getString(eq(ASSEMBLIES_TO_SCAN_KEY),
        anyString())).thenReturn("foo/bar/whatever/*.dll");

    when(vsProject1.getGeneratedAssemblies(BUILD_CONFIGURATIONS_DEFVALUE)).thenReturn(Collections.singleton(new File("toto")));
    assertTrue(cilSensor.shouldExecuteOnProject(project));
  }


}
