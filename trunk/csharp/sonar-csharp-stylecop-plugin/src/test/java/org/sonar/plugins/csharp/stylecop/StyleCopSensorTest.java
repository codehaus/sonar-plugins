/*
 * Sonar C# Plugin :: StyleCop
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

package org.sonar.plugins.csharp.stylecop;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.csharp.api.CSharpConfiguration;

import com.google.common.collect.Lists;

public class StyleCopSensorTest {

  @Test
  public void testShouldExecuteOnProject() throws Exception {
    Configuration conf = new BaseConfiguration();
    StyleCopSensor sensor = new StyleCopSensor(null, null, null, null, null, new CSharpConfiguration(conf));

    Project project = mock(Project.class);
    when(project.getLanguageKey()).thenReturn("java");
    assertFalse(sensor.shouldExecuteOnProject(project));

    when(project.getLanguageKey()).thenReturn("cs");
    assertTrue(sensor.shouldExecuteOnProject(project));

    conf.addProperty(StyleCopConstants.MODE, StyleCopConstants.MODE_SKIP);
    sensor = new StyleCopSensor(null, null, null, null, null, new CSharpConfiguration(conf));
    assertFalse(sensor.shouldExecuteOnProject(project));
  }

  @Test
  public void testAnalyseResults() throws Exception {
    StyleCopResultParser parser = mock(StyleCopResultParser.class);
    StyleCopSensor sensor = new StyleCopSensor(null, null, null, null, parser, new CSharpConfiguration(new BaseConfiguration()));

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
    StyleCopSensor sensor = new StyleCopSensor(fileSystem, null, null, null, null, new CSharpConfiguration(conf));

    Collection<File> reportFiles = sensor.getReportFilesList();
    assertThat(reportFiles.size(), is(1));
    assertThat(reportFiles, hasItems(new File("target/sonar", StyleCopConstants.STYLECOP_REPORT_XML)));
  }

  @Test
  public void testGetReportFilesListInReuseMode() throws Exception {
    ProjectFileSystem fileSystem = mock(ProjectFileSystem.class);
    when(fileSystem.getBuildDir()).thenReturn(new File("target"));
    Configuration conf = new BaseConfiguration();
    conf.addProperty(StyleCopConstants.MODE, StyleCopConstants.MODE_REUSE_REPORT);
    conf.addProperty(StyleCopConstants.REPORTS_PATH_KEY, "foo.xml,folder/bar.xml");
    StyleCopSensor sensor = new StyleCopSensor(fileSystem, null, null, null, null, new CSharpConfiguration(conf));

    Collection<File> reportFiles = sensor.getReportFilesList();
    assertThat(reportFiles.size(), is(2));
    assertThat(reportFiles, hasItems(new File("target/foo.xml"), new File("target/folder/bar.xml")));
  }

}
