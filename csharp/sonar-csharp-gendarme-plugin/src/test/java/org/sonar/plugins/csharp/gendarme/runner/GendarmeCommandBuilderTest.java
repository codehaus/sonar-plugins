/*
 * Sonar C# Plugin :: Gendarme
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

package org.sonar.plugins.csharp.gendarme.runner;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.csharp.api.CSharpConfiguration;
import org.sonar.plugins.csharp.api.MicrosoftWindowsEnvironment;
import org.sonar.plugins.csharp.api.utils.Command;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioProject;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioSolution;
import org.sonar.plugins.csharp.gendarme.GendarmeConstants;

import com.google.common.collect.Lists;

public class GendarmeCommandBuilderTest {

  private static GendarmeCommandBuilder gendarmeCommandBuilder;
  private static Configuration configuration;
  private static CSharpConfiguration cSharpConfiguration;
  private static ProjectFileSystem projectFileSystem;
  private static MicrosoftWindowsEnvironment microsoftWindowsEnvironment;
  private static VisualStudioProject project;
  private static File fakeGendarmeExecutable;
  private static File fakeGendarmeConfigFile;

  @BeforeClass
  public static void initStatic() throws Exception {
    fakeGendarmeExecutable = FileUtils.toFile(GendarmeCommandBuilderTest.class.getResource("/Runner/FakeProg/gendarme.exe"));
    fakeGendarmeConfigFile = FileUtils.toFile(GendarmeCommandBuilderTest.class.getResource("/Runner/FakeGendarmeConfigFile.xml"));
    configuration = new BaseConfiguration();
    cSharpConfiguration = new CSharpConfiguration(configuration);

    projectFileSystem = mock(ProjectFileSystem.class);
    when(projectFileSystem.getBasedir()).thenReturn(FileUtils.toFile(GendarmeCommandBuilderTest.class.getResource("/Runner")));

    microsoftWindowsEnvironment = mock(MicrosoftWindowsEnvironment.class);
    VisualStudioSolution solution = mock(VisualStudioSolution.class);
    when(microsoftWindowsEnvironment.getCurrentSolution()).thenReturn(solution);
    project = mock(VisualStudioProject.class);
    when(solution.getProjects()).thenReturn(Lists.newArrayList(project));
  }

  @Before
  public void init() throws Exception {
    configuration.clear();
    configuration.addProperty(GendarmeConstants.EXECUTABLE_KEY, fakeGendarmeExecutable.getAbsolutePath());
  }

  @Test
  public void testToArray() throws Exception {
    configuration.addProperty(GendarmeConstants.ASSEMBLIES_TO_SCAN_KEY, "FakeAssemblies/Fake1.assembly, FakeAssemblies/Fake2.assembly");
    configuration.addProperty(GendarmeConstants.ASSEMBLIES_TO_SCAN_KEY, "FakeDepFolder, UnexistingFolder");

    gendarmeCommandBuilder = new GendarmeCommandBuilder(cSharpConfiguration, projectFileSystem, microsoftWindowsEnvironment);
    gendarmeCommandBuilder.setGendarmeConfigFile(fakeGendarmeConfigFile);
    Command command = gendarmeCommandBuilder.createCommand();

    assertThat(command.getExecutable(), endsWith("gendarme.exe"));
    String[] commands = command.getArguments().toArray(new String[] {});
    assertThat(commands[1], endsWith("FakeGendarmeConfigFile.xml"));
    assertThat(commands[3], endsWith("gendarme-report.xml"));
    assertThat(commands[4], is("--quiet"));
    assertThat(commands[6], is("normal+"));
    assertThat(commands[8], is("all"));
    assertThat(commands[9], endsWith("Fake1.assembly"));
    assertThat(commands[10], endsWith("Fake2.assembly"));
  }

  @Test
  public void testToArrayWithOtherCustomParams() throws Exception {
    configuration.addProperty(GendarmeConstants.ASSEMBLIES_TO_SCAN_KEY, "FakeAssemblies/Fake1.assembly");
    configuration.addProperty(GendarmeConstants.GENDARME_CONFIDENCE_KEY, "total");

    gendarmeCommandBuilder = new GendarmeCommandBuilder(cSharpConfiguration, projectFileSystem, microsoftWindowsEnvironment);
    gendarmeCommandBuilder.setGendarmeConfigFile(fakeGendarmeConfigFile);
    Command command = gendarmeCommandBuilder.createCommand();

    assertThat(command.getExecutable(), endsWith("gendarme.exe"));
    String[] commands = command.getArguments().toArray(new String[] {});
    assertThat(commands[1], endsWith("FakeGendarmeConfigFile.xml"));
    assertThat(commands[3], endsWith("gendarme-report.xml"));
    assertThat(commands[4], is("--quiet"));
    assertThat(commands[6], is("total"));
    assertThat(commands[8], is("all"));
    assertThat(commands[9], endsWith("Fake1.assembly"));
    assertThat(commands.length, is(10));
  }

  @Test
  public void testToArrayWithNoConfigButExistingDebugAssembly() throws Exception {
    when(project.isTest()).thenReturn(false);
    when(project.getDebugArtifact()).thenReturn(
        FileUtils.toFile(GendarmeCommandBuilderTest.class.getResource("/Runner/FakeAssemblies/Fake1.assembly")));

    gendarmeCommandBuilder = new GendarmeCommandBuilder(cSharpConfiguration, projectFileSystem, microsoftWindowsEnvironment);
    gendarmeCommandBuilder.setGendarmeConfigFile(fakeGendarmeConfigFile);
    Command command = gendarmeCommandBuilder.createCommand();

    String[] commands = command.getArguments().toArray(new String[] {});
    assertThat(commands[9], endsWith("Fake1.assembly"));
  }

  @Test
  public void testToArrayWithNoConfigButExistingReleseAssembly() throws Exception {
    when(project.isTest()).thenReturn(false);
    when(project.getDebugArtifact()).thenReturn(null);
    when(project.getReleaseArtifact()).thenReturn(
        FileUtils.toFile(GendarmeCommandBuilderTest.class.getResource("/Runner/FakeAssemblies/Fake1.assembly")));

    gendarmeCommandBuilder = new GendarmeCommandBuilder(cSharpConfiguration, projectFileSystem, microsoftWindowsEnvironment);
    gendarmeCommandBuilder.setGendarmeConfigFile(fakeGendarmeConfigFile);
    Command command = gendarmeCommandBuilder.createCommand();

    String[] commands = command.getArguments().toArray(new String[] {});
    assertThat(commands[9], endsWith("Fake1.assembly"));
  }

  @Test(expected = IllegalStateException.class)
  public void testToArrayWithNoConfigAndNonExistingReleseAssembly() throws Exception {
    when(project.isTest()).thenReturn(false);
    when(project.getDebugArtifact()).thenReturn(null);
    when(project.getReleaseArtifact())
        .thenReturn(FileUtils.toFile(GendarmeCommandBuilderTest.class.getResource("/Runner/FakeAssemblies/")));

    gendarmeCommandBuilder = new GendarmeCommandBuilder(cSharpConfiguration, projectFileSystem, microsoftWindowsEnvironment);
    gendarmeCommandBuilder.setGendarmeConfigFile(fakeGendarmeConfigFile);
    gendarmeCommandBuilder.createCommand();
  }

  @Test(expected = IllegalStateException.class)
  public void testOnlyTestProject() throws Exception {
    when(project.isTest()).thenReturn(true);
    gendarmeCommandBuilder = new GendarmeCommandBuilder(cSharpConfiguration, projectFileSystem, microsoftWindowsEnvironment);
    gendarmeCommandBuilder.setGendarmeConfigFile(fakeGendarmeConfigFile);
    gendarmeCommandBuilder.createCommand();
  }

  @Test(expected = IllegalStateException.class)
  public void testWithNullGendarmeConfigFile() throws Exception {
    when(project.isTest()).thenReturn(false);
    when(project.getDebugArtifact()).thenReturn(
        FileUtils.toFile(GendarmeCommandBuilderTest.class.getResource("/Runner/FakeAssemblies/Fake1.assembly")));

    gendarmeCommandBuilder = new GendarmeCommandBuilder(cSharpConfiguration, projectFileSystem, microsoftWindowsEnvironment);
    gendarmeCommandBuilder.setGendarmeConfigFile(null);
    gendarmeCommandBuilder.createCommand();
  }

}
