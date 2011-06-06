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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioProject;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioSolution;
import org.sonar.test.TestUtils;

import com.google.common.collect.Lists;

public class GendarmeRunnerTest {

  public VisualStudioProject project;
  public VisualStudioSolution solution;

  @Before
  public void initData() {
    project = mock(VisualStudioProject.class);
    solution = mock(VisualStudioSolution.class);
    when(project.getReleaseArtifact()).thenReturn(TestUtils.getResource("/runner/FakeAssemblies/Fake1.assembly"));
    when(solution.getProjects()).thenReturn(Lists.newArrayList(project));
  }

  @Test
  public void testCreateCommandBuilderForSolution() throws Exception {
    String fakeExec = TestUtils.getResource("/runner/FakeGendarmeExecutable.txt").getAbsolutePath();
    GendarmeRunner runner = GendarmeRunner.create(fakeExec, new File("target/sonar/tempFolder").getAbsolutePath());
    GendarmeCommandBuilder builder = runner.createCommandBuilder(solution);
    builder.setConfigFile(TestUtils.getResource("/runner/FakeGendarmeConfigFile.xml"));
    builder.setReportFile(new File("gendarme-report.xml"));
    assertThat(builder.toCommand().getExecutable(), is(fakeExec));
  }

  @Test
  public void testCreateCommandBuilderForProject() throws Exception {
    String fakeExec = TestUtils.getResource("/runner/FakeGendarmeExecutable.txt").getAbsolutePath();
    GendarmeRunner runner = GendarmeRunner.create(fakeExec, new File("target/sonar/tempFolder").getAbsolutePath());
    GendarmeCommandBuilder builder = runner.createCommandBuilder(project);
    builder.setConfigFile(TestUtils.getResource("/runner/FakeGendarmeConfigFile.xml"));
    builder.setReportFile(new File("gendarme-report.xml"));
    assertThat(builder.toCommand().getExecutable(), is(fakeExec));
  }

}
