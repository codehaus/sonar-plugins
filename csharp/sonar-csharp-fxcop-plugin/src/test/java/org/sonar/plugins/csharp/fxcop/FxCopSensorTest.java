/*
 * Sonar C# Plugin :: FxCop
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

package org.sonar.plugins.csharp.fxcop;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.rules.ActiveRule;
import org.sonar.plugins.csharp.api.CSharpConfiguration;
import org.sonar.plugins.csharp.fxcop.profiles.FxCopProfileExporter;
import org.sonar.plugins.csharp.fxcop.runner.FxCopRunner;

import com.google.common.collect.Lists;

public class FxCopSensorTest {

  @Test
  public void testExecuteWithoutRule() throws Exception {
    FxCopRunner runner = mock(FxCopRunner.class);
    RulesProfile rulesProfile = mock(RulesProfile.class);
    when(rulesProfile.getActiveRulesByRepository(anyString())).thenReturn(new ArrayList<ActiveRule>());
    FxCopSensor sensor = new FxCopSensor(null, rulesProfile, runner, null, null, new CSharpConfiguration(new BaseConfiguration()));
    sensor.analyse(null, null);
    verify(runner, never()).execute(any(File.class));
  }

  @Test
  public void testShouldExecuteOnProject() throws Exception {
    Configuration conf = new BaseConfiguration();
    FxCopSensor sensor = new FxCopSensor(null, null, null, null, null, new CSharpConfiguration(conf));

    Project project = mock(Project.class);
    when(project.getLanguageKey()).thenReturn("java");
    assertFalse(sensor.shouldExecuteOnProject(project));

    when(project.getLanguageKey()).thenReturn("cs");
    assertTrue(sensor.shouldExecuteOnProject(project));

    conf.addProperty(FxCopConstants.MODE, FxCopConstants.MODE_SKIP);
    sensor = new FxCopSensor(null, null, null, null, null, new CSharpConfiguration(conf));
    assertFalse(sensor.shouldExecuteOnProject(project));
  }

  @Test
  public void testAnalyseResults() throws Exception {
    FxCopResultParser parser = mock(FxCopResultParser.class);
    FxCopSensor sensor = new FxCopSensor(null, null, null, null, parser, new CSharpConfiguration(new BaseConfiguration()));

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
    FxCopSensor sensor = new FxCopSensor(fileSystem, null, null, null, null, new CSharpConfiguration(conf));

    Collection<File> reportFiles = sensor.getReportFilesList();
    assertThat(reportFiles.size(), is(1));
    assertThat(reportFiles, hasItems(new File("target/sonar", FxCopConstants.FXCOP_REPORT_XML)));
  }

  @Test
  public void testGetReportFilesListInReuseMode() throws Exception {
    ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
    when(fileSystem.getBuildDir()).thenReturn(new File("target"));
    Configuration conf = new BaseConfiguration();
    conf.addProperty(FxCopConstants.MODE, FxCopConstants.MODE_REUSE_REPORT);
    conf.addProperty(FxCopConstants.REPORTS_PATH_KEY, "foo.xml,folder/bar.xml");
    FxCopSensor sensor = new FxCopSensor(fileSystem, null, null, null, null, new CSharpConfiguration(conf));

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
    FxCopProfileExporter profileExporter = mock(FxCopProfileExporter.class);
    doAnswer(new Answer<Object>() {

      public Object answer(InvocationOnMock invocation) throws IOException {
        FileWriter writer = (FileWriter) invocation.getArguments()[1];
        writer.write("Hello");
        return null;
      }
    }).when(profileExporter).exportProfile((RulesProfile) anyObject(), (FileWriter) anyObject());
    FxCopSensor sensor = new FxCopSensor(fileSystem, null, null, profileExporter, null, new CSharpConfiguration(new BaseConfiguration()));

    sensor.generateConfigurationFile();
    File report = new File(sonarDir, FxCopConstants.FXCOP_RULES_FILE);
    assertTrue(report.exists());
    report.delete();
  }

}
