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

package org.sonar.plugins.csharp.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.csharp.api.CSharpConstants;
import org.sonar.plugins.csharp.api.MicrosoftWindowsEnvironment;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioSolution;

public class MicrosoftWindowsEnvironmentSensorTest {

  private static File fakeSdkDir;
  private MicrosoftWindowsEnvironmentSensor sensor;
  private Configuration conf;
  private ProjectFileSystem fileSystem;

  @BeforeClass
  public static void initResources() {
    System.setProperty("MicrosoftWindowsEnvironment.allowOverrideAttributes", "true");
    fakeSdkDir = new File("Sonar/SDK");
    fakeSdkDir.mkdirs();
  }

  @AfterClass
  public static void removeResources() {
    fakeSdkDir.delete();
  }

  @Before
  public void initSensor() {
    conf = new BaseConfiguration();
    conf.addProperty(CSharpConstants.DOTNET_SDK_DIR_KEY, fakeSdkDir.getAbsolutePath());
    fileSystem = mock(ProjectFileSystem.class);
    when(fileSystem.getBasedir()).thenReturn(FileUtils.toFile(getClass().getResource("/solution/Example")));
    sensor = new MicrosoftWindowsEnvironmentSensor(conf, fileSystem);
  }

  @Test(expected = SonarException.class)
  public void testNotValidSdkDir() throws Exception {
    conf = new BaseConfiguration();
    conf.addProperty(CSharpConstants.DOTNET_SDK_DIR_KEY, "foo");
    fileSystem = mock(ProjectFileSystem.class);
    sensor = new MicrosoftWindowsEnvironmentSensor(conf, fileSystem);
    sensor.analyse(null, null);
  }

  @Test
  public void testCorrectlyConfiguredProject() throws Exception {
    conf.addProperty(CSharpConstants.SOLUTION_FILE_KEY, "Example.sln");
    sensor.analyse(null, null);
    assertThat(MicrosoftWindowsEnvironment.getDotnetSdkDirectory().getAbsolutePath(), is(fakeSdkDir.getAbsolutePath()));
    VisualStudioSolution solution = MicrosoftWindowsEnvironment.getCurrentSolution();
    assertNotNull(solution);
    assertThat(solution.getProjects().size(), is(3));
  }

  @Test(expected = SonarException.class)
  public void testNonExistingSlnFile() throws Exception {
    conf.addProperty(CSharpConstants.SOLUTION_FILE_KEY, "NonExistingFile.sln");
    sensor.analyse(null, null);
  }

  @Test
  public void testNoSpecifiedSlnFileButOneFound() throws Exception {
    conf.addProperty(CSharpConstants.SOLUTION_FILE_KEY, "");
    sensor.analyse(null, null);
    assertThat(MicrosoftWindowsEnvironment.getDotnetSdkDirectory().getAbsolutePath(), is(fakeSdkDir.getAbsolutePath()));
    VisualStudioSolution solution = MicrosoftWindowsEnvironment.getCurrentSolution();
    assertNotNull(solution);
    assertThat(solution.getProjects().size(), is(3));
  }

  @Test(expected = SonarException.class)
  public void testNoSpecifiedSlnFileButNoneFound() throws Exception {
    conf.addProperty(CSharpConstants.SOLUTION_FILE_KEY, "");
    when(fileSystem.getBasedir()).thenReturn(FileUtils.toFile(getClass().getResource("/solution")));
    sensor.analyse(null, null);
  }

  @Test(expected = SonarException.class)
  public void testNoSpecifiedSlnFileButTooManyFound() throws Exception {
    conf.addProperty(CSharpConstants.SOLUTION_FILE_KEY, "");
    when(fileSystem.getBasedir()).thenReturn(FileUtils.toFile(getClass().getResource("/solution/FakeSolutionWithTwoSlnFiles")));
    sensor.analyse(null, null);
  }

}
