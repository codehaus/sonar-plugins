/*
 * .NET tools :: FxCop Runner
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
package org.sonar.dotnet.tools.fxcop;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.api.utils.command.Command;
import org.sonar.dotnet.tools.commons.visualstudio.VisualStudioProject;
import org.sonar.dotnet.tools.commons.visualstudio.VisualStudioSolution;
import org.sonar.test.TestUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class FxCopCommandBuilderTest {

  private static File fakeFxCopExecutable;
  private static File fakeFxCopConfigFile;
  private static File fakeFxCopReportFile;
  private static File silverlightFolder;
  private VisualStudioSolution solution;
  private VisualStudioProject vsProject;
  private FxCopCommandBuilder fxCopCommandBuilder;

  @BeforeClass
  public static void initStatic() throws Exception {
    fakeFxCopExecutable = TestUtils.getResource("/Runner/FakeProg/FxCopCmd.exe");
    fakeFxCopConfigFile = TestUtils.getResource("/Runner/FakeFxCopConfigFile.xml");
    silverlightFolder = TestUtils.getResource("/Runner/SilverlightFolder");
    fakeFxCopReportFile = new File("target/sonar/FxCop/fxcop-report.xml");
  }

  @Before
  public void init() throws Exception {
    solution = mock(VisualStudioSolution.class);
    vsProject = mock(VisualStudioProject.class);
    when(vsProject.getGeneratedAssemblies("Debug")).thenReturn(
        Sets.newHashSet(TestUtils.getResource("/Runner/FakeAssemblies/Fake1.assembly")));
    when(vsProject.getDirectory()).thenReturn(TestUtils.getResource("/Runner"));
    when(solution.getProjects()).thenReturn(Lists.newArrayList(vsProject));
    when(solution.getSolutionDir()).thenReturn(TestUtils.getResource("/Runner"));
    fxCopCommandBuilder = FxCopCommandBuilder.createBuilder(solution, vsProject);
    fxCopCommandBuilder.setExecutable(fakeFxCopExecutable);
    fxCopCommandBuilder.setTimeoutMinutes(10);
    fxCopCommandBuilder.setConfigFile(fakeFxCopConfigFile);
    fxCopCommandBuilder.setReportFile(fakeFxCopReportFile);
    fxCopCommandBuilder.setIgnoreGeneratedCode(true);
  }

  @Test
  public void testToCommandForVSProject() throws Exception {

    fxCopCommandBuilder.setAssemblyDependencyDirectories("$(SolutionDir)/FakeDepFolder", "UnexistingFolder");

    Command command = fxCopCommandBuilder.toCommand();
    assertThat(toUnixStyle(command.getExecutable()), endsWith("/Runner/FakeProg/FxCopCmd.exe"));
    String[] commands = command.getArguments().toArray(new String[] {});
    assertThat(commands[0], endsWith("FakeFxCopConfigFile.xml"));
    assertThat(commands[1], endsWith("fxcop-report.xml"));
    assertThat(commands[2], endsWith("Fake1.assembly"));
    assertThat(commands[3], endsWith("FakeDepFolder"));
    assertThat(commands[4], endsWith("/igc"));
    assertThat(commands[5], endsWith("/to:600"));
    assertThat(commands[6], endsWith("/gac"));
  }

  @Test
  public void testToCommandForWebVSProject() throws Exception {
    when(vsProject.isWebProject()).thenReturn(true);

    fxCopCommandBuilder.setAssemblyDependencyDirectories("$(SolutionDir)/FakeDepFolder", "UnexistingFolder");

    String[] commands = fxCopCommandBuilder.toCommand().getArguments().toArray(new String[] {});
    assertThat(commands[7], endsWith("/aspnet"));
  }
  
  @Test
  public void testToCommandForSilverlightProject() throws Exception {
    when(vsProject.isSilverlightProject()).thenReturn(true);

    fxCopCommandBuilder.setAssemblyDependencyDirectories("$(SolutionDir)/FakeDepFolder", "UnexistingFolder");
    fxCopCommandBuilder.setSilverlightFolder(silverlightFolder);

    String[] commands = fxCopCommandBuilder.toCommand().getArguments().toArray(new String[] {});
    assertThat(commands[4], endsWith("SilverlightFolder"));
  }

  @Test(expected = FxCopException.class)
  public void testToCommandForSilverlightProjectWithoutSilverlightFolder() throws Exception {
    when(vsProject.isSilverlightProject()).thenReturn(true);

    fxCopCommandBuilder.setAssemblyDependencyDirectories("$(SolutionDir)/FakeDepFolder", "UnexistingFolder");
    fxCopCommandBuilder.setSilverlightFolder(null);

    fxCopCommandBuilder.toCommand();
  }

  @Test(expected = FxCopException.class)
  public void testToCommandForSilverlightProjectWithInexistingSilverlightFolder() throws Exception {
    when(vsProject.isSilverlightProject()).thenReturn(true);

    fxCopCommandBuilder.setAssemblyDependencyDirectories("$(SolutionDir)/FakeDepFolder", "UnexistingFolder");
    fxCopCommandBuilder.setSilverlightFolder(new File("Foo"));

    fxCopCommandBuilder.toCommand();
  }

  @Test
  public void testToCommandWithSpecifiedAssemblies() throws Exception {

    fxCopCommandBuilder.setAssemblyDependencyDirectories("$(SolutionDir)/FakeDepFolder", "UnexistingFolder");
    fxCopCommandBuilder.setAssembliesToScan("FakeAssemblies/Fake1.assembly", "FakeAssemblies/Fake2.assembly");

    Command command = fxCopCommandBuilder.toCommand();
    assertThat(toUnixStyle(command.getExecutable()), endsWith("/Runner/FakeProg/FxCopCmd.exe"));
    String[] commands = command.getArguments().toArray(new String[] {});
    assertThat(commands[0], endsWith("FakeFxCopConfigFile.xml"));
    assertThat(commands[1], endsWith("fxcop-report.xml"));
    assertThat(commands[2], endsWith(".assembly"));
    assertThat(commands[3], endsWith(".assembly"));
    assertThat(commands[4], endsWith("FakeDepFolder"));
    assertThat(commands[5], endsWith("/igc"));
    assertThat(commands[6], endsWith("/to:600"));
    assertThat(commands[7], endsWith("/gac"));
  }

  @Test
  public void testToCommandWithOtherCustomParams() throws Exception {
    fxCopCommandBuilder.setTimeoutMinutes(1000);
    fxCopCommandBuilder.setIgnoreGeneratedCode(false);
    fxCopCommandBuilder.setBuildConfigurations("Debug");

    Command command = fxCopCommandBuilder.toCommand();
    assertThat(toUnixStyle(command.getExecutable()), endsWith("/Runner/FakeProg/FxCopCmd.exe"));
    String[] commands = command.getArguments().toArray(new String[] {});
    assertThat(commands[0], endsWith("FakeFxCopConfigFile.xml"));
    assertThat(commands[1], endsWith("fxcop-report.xml"));
    assertThat(commands[2], endsWith("Fake1.assembly"));
    assertThat(commands[3], endsWith("/to:60000"));
    assertThat(commands[4], endsWith("/gac"));
  }

  @Test(expected = IllegalStateException.class)
  public void testToCommandWithNoConfigFile() throws Exception {
    when(vsProject.isTest()).thenReturn(true);

    FxCopCommandBuilder fxCopCommandBuilder = FxCopCommandBuilder.createBuilder(solution, vsProject);
    fxCopCommandBuilder.setExecutable(fakeFxCopExecutable);
    fxCopCommandBuilder.setReportFile(fakeFxCopReportFile);

    fxCopCommandBuilder.toCommand();
  }

  private String toUnixStyle(String path) {
    return path.replaceAll("\\\\", "/");
  }

}
