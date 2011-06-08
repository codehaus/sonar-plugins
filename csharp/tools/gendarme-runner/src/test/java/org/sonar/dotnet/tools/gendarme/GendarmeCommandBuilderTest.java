/*
 * .NET tools :: Gendarme Runner
 * Copyright (C) 2011 Jose Chillan, Alexandre Victoor and SonarSource
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

package org.sonar.dotnet.tools.gendarme;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.api.utils.command.Command;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioProject;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioSolution;
import org.sonar.test.TestUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class GendarmeCommandBuilderTest {

  public static VisualStudioProject vsProject;
  public static VisualStudioSolution solution;

  @BeforeClass
  public static void initData() {
    vsProject = mock(VisualStudioProject.class);
    solution = mock(VisualStudioSolution.class);
    when(vsProject.getGeneratedAssemblies("Debug")).thenReturn(
        Sets.newHashSet(TestUtils.getResource("/runner/FakeAssemblies/Fake1.assembly")));
    when(solution.getProjects()).thenReturn(Lists.newArrayList(vsProject));
  }

  @Test
  public void testToCommandForSolution() throws Exception {
    GendarmeCommandBuilder builder = GendarmeCommandBuilder.createBuilder(solution);
    builder.setExecutable(new File("gendarme.exe"));
    builder.setConfigFile(TestUtils.getResource("/runner/FakeGendarmeConfigFile.xml"));
    builder.setReportFile(new File("gendarme-report.xml"));
    Command command = builder.toCommand();

    assertThat(command.getExecutable(), endsWith("gendarme.exe"));
    String[] commands = command.getArguments().toArray(new String[] {});
    assertThat(commands.length, is(10));
    assertThat(commands[1], endsWith("FakeGendarmeConfigFile.xml"));
    assertThat(commands[3], endsWith("gendarme-report.xml"));
    assertThat(commands[4], is("--quiet"));
    assertThat(commands[6], is("normal+"));
    assertThat(commands[8], is("all"));
    assertThat(commands[9], endsWith("Fake1.assembly"));
  }

  @Test
  public void testToCommandForProject() throws Exception {
    GendarmeCommandBuilder builder = GendarmeCommandBuilder.createBuilder(vsProject);
    builder.setExecutable(new File("gendarme.exe"));
    builder.setConfigFile(TestUtils.getResource("/runner/FakeGendarmeConfigFile.xml"));
    builder.setReportFile(new File("gendarme-report.xml"));
    Command command = builder.toCommand();

    assertThat(command.getExecutable(), endsWith("gendarme.exe"));
    String[] commands = command.getArguments().toArray(new String[] {});
    assertThat(commands.length, is(10));
    assertThat(commands[1], endsWith("FakeGendarmeConfigFile.xml"));
    assertThat(commands[3], endsWith("gendarme-report.xml"));
    assertThat(commands[4], is("--quiet"));
    assertThat(commands[6], is("normal+"));
    assertThat(commands[8], is("all"));
    assertThat(commands[9], endsWith("Fake1.assembly"));
  }

  @Test(expected = IllegalStateException.class)
  public void testNoSolutionOrProject() throws Exception {
    GendarmeCommandBuilder builder = GendarmeCommandBuilder.createBuilder((VisualStudioSolution) null);
    builder.toCommand();
  }

  @Test(expected = IllegalStateException.class)
  public void testOnlyTestProject() throws Exception {
    when(vsProject.isTest()).thenReturn(true);
    GendarmeCommandBuilder builder = GendarmeCommandBuilder.createBuilder(solution);
    builder.toCommand();
  }

  @Test(expected = IllegalStateException.class)
  public void testToArrayWithNoConfig() throws Exception {
    GendarmeCommandBuilder builder = GendarmeCommandBuilder.createBuilder(solution);
    builder.toCommand();
  }

  @Test(expected = IllegalStateException.class)
  public void testToArrayWithNoExistingAssembly() throws Exception {
    when(vsProject.getGeneratedAssemblies("Debug")).thenReturn(
        Sets.newHashSet(TestUtils.getResource("/Runner/FakeAssemblies/Unexisting.assembly")));
    GendarmeCommandBuilder builder = GendarmeCommandBuilder.createBuilder(solution);
    builder.setConfigFile(TestUtils.getResource("/runner/FakeGendarmeConfigFile.xml"));
    builder.toCommand();
  }

}
