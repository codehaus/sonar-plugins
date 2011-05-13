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

package org.sonar.plugins.csharp.gendarme;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.utils.command.Command;
import org.sonar.plugins.csharp.api.CSharpConfiguration;
import org.sonar.plugins.csharp.api.MicrosoftWindowsEnvironment;
import org.sonar.plugins.csharp.api.runners.gendarme.GendarmeRunner;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioProject;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioSolution;
import org.sonar.plugins.csharp.gendarme.profiles.GendarmeProfileExporter;
import org.sonar.plugins.csharp.gendarme.results.GendarmeResultParser;

import com.google.common.collect.Lists;

public class GendarmeSensorTest {

  @Test
  public void testLaunchGendarme() throws Exception {
    ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
    when(fileSystem.getSonarWorkingDirectory()).thenReturn(FileUtils.toFile(getClass().getResource("/Runner")));
    MicrosoftWindowsEnvironment microsoftWindowsEnvironment = mock(MicrosoftWindowsEnvironment.class);
    VisualStudioProject project = mock(VisualStudioProject.class);
    VisualStudioSolution solution = mock(VisualStudioSolution.class);
    when(project.getReleaseArtifact()).thenReturn(FileUtils.toFile(getClass().getResource("/Sensor/FakeAssemblies/Fake1.assembly")));
    when(solution.getProjects()).thenReturn(Lists.newArrayList(project));
    when(microsoftWindowsEnvironment.getCurrentSolution()).thenReturn(solution);
    GendarmeSensor sensor = new GendarmeSensor(fileSystem, null, null, null, new CSharpConfiguration(new BaseConfiguration()),
        microsoftWindowsEnvironment);

    GendarmeRunner runner = mock(GendarmeRunner.class);
    sensor.launchGendarme(runner, FileUtils.toFile(getClass().getResource("/Sensor/FakeGendarmeConfigFile.xml")));
    verify(runner).execute(any(Command.class), eq(10));
  }

  @Test
  public void testShouldExecuteOnProject() throws Exception {
    Configuration conf = new BaseConfiguration();
    GendarmeSensor sensor = new GendarmeSensor(null, null, null, null, new CSharpConfiguration(conf), null);

    Project project = mock(Project.class);
    when(project.getLanguageKey()).thenReturn("java");
    assertFalse(sensor.shouldExecuteOnProject(project));

    when(project.getLanguageKey()).thenReturn("cs");
    assertTrue(sensor.shouldExecuteOnProject(project));

    conf.addProperty(GendarmeConstants.MODE, GendarmeConstants.MODE_SKIP);
    sensor = new GendarmeSensor(null, null, null, null, new CSharpConfiguration(conf), null);
    assertFalse(sensor.shouldExecuteOnProject(project));
  }

  @Test
  public void testAnalyseResults() throws Exception {
    GendarmeResultParser parser = mock(GendarmeResultParser.class);
    GendarmeSensor sensor = new GendarmeSensor(null, null, null, parser, new CSharpConfiguration(new BaseConfiguration()), null);

    File tempFile = File.createTempFile("foo", null);
    List<File> reports = Lists.newArrayList(tempFile, new File("bar"));
    sensor.analyseResults(reports);
    tempFile.delete();
    verify(parser).parse(tempFile);
  }

  @Test
  public void testGetReportFilesList() throws Exception {
    ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
    when(fileSystem.getSonarWorkingDirectory()).thenReturn(new File("target/sonar"));
    Configuration conf = new BaseConfiguration();
    GendarmeSensor sensor = new GendarmeSensor(fileSystem, null, null, null, new CSharpConfiguration(conf), null);

    Collection<File> reportFiles = sensor.getReportFilesList();
    assertThat(reportFiles.size(), is(1));
    assertThat(reportFiles, hasItems(new File("target/sonar", GendarmeConstants.GENDARME_REPORT_XML)));
  }

  @Test
  public void testGetReportFilesListInReuseMode() throws Exception {
    ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
    when(fileSystem.getBuildDir()).thenReturn(new File("target"));
    Configuration conf = new BaseConfiguration();
    conf.addProperty(GendarmeConstants.MODE, GendarmeConstants.MODE_REUSE_REPORT);
    conf.addProperty(GendarmeConstants.REPORTS_PATH_KEY, "foo.xml,folder/bar.xml");
    GendarmeSensor sensor = new GendarmeSensor(fileSystem, null, null, null, new CSharpConfiguration(conf), null);

    Collection<File> reportFiles = sensor.getReportFilesList();
    assertThat(reportFiles.size(), is(2));
    assertThat(reportFiles, hasItems(new File("target/foo.xml"), new File("target/folder/bar.xml")));
  }

  @Test
  public void testGenerateConfigurationFile() throws Exception {
    File sonarDir = new File("target/sonar");
    sonarDir.mkdirs();
    ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
    when(fileSystem.getSonarWorkingDirectory()).thenReturn(sonarDir);
    GendarmeProfileExporter profileExporter = mock(GendarmeProfileExporter.class);
    doAnswer(new Answer<Object>() {

      public Object answer(InvocationOnMock invocation) throws IOException {
        FileWriter writer = (FileWriter) invocation.getArguments()[1];
        writer.write("Hello");
        return null;
      }
    }).when(profileExporter).exportProfile((RulesProfile) anyObject(), (FileWriter) anyObject());
    GendarmeSensor sensor = new GendarmeSensor(fileSystem, null, profileExporter, null, new CSharpConfiguration(new BaseConfiguration()),
        null);

    sensor.generateConfigurationFile();
    File report = new File(sonarDir, GendarmeConstants.GENDARME_RULES_FILE);
    assertTrue(report.exists());
    report.delete();
  }

}
