/*
 * Sonar Scala Plugin
 * Copyright (C) 2011 Felix Müller
 * felix.mueller.berlin@googlemail.com
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
package org.sonar.plugins.scala.language;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;

public class BaseMetricsSensorTest {

  private BaseMetricsSensor baseMetricsSensor;

  private ProjectFileSystem fileSystem;
  private Project project;
  private SensorContext sensorContext;

  @Before
  public void setUp() {
    baseMetricsSensor = new BaseMetricsSensor(Scala.INSTANCE);

    fileSystem = mock(ProjectFileSystem.class);
    when(fileSystem.getSourceCharset()).thenReturn(Charset.defaultCharset());

    project = mock(Project.class);
    when(project.getFileSystem()).thenReturn(fileSystem);

    sensorContext = mock(SensorContext.class);
  }

  @Test
  public void shouldIncrementFileMetricForOneScalaFile() {
    analyseOneScalaFile();
    verify(sensorContext, times(1)).saveMeasure(
        eq(FileTestUtils.SCALA_SOURCE_FILE), eq(CoreMetrics.FILES), eq(1.0));
  }

  @Test
  public void shouldIncrementPackageMetricForOneScalaFile() {
    analyseOneScalaFile();
    verify(sensorContext, times(1)).saveMeasure(
        any(ScalaPackage.class), eq(CoreMetrics.PACKAGES), eq(1.0));
  }

  @Test
  public void shouldIncreaseFileMetricForAllScalaFiles() throws IOException {
    analyseAllScalaFiles();
    verify(sensorContext, times(3)).saveMeasure(
        eq(FileTestUtils.SCALA_SOURCE_FILE), eq(CoreMetrics.FILES), eq(1.0));
  }

  @Test
  public void shouldIncreasePackageMetricForAllScalaFiles() {
    analyseAllScalaFiles();
    verify(sensorContext, times(2)).saveMeasure(
        any(ScalaPackage.class), eq(CoreMetrics.PACKAGES), eq(1.0));
  }

  private void analyseOneScalaFile() {
    analyseScalaFiles(1);
  }

  private void analyseAllScalaFiles() {
    analyseScalaFiles(3);
  }

  private void analyseScalaFiles(int numberOfFiles) {
    when(fileSystem.mainFiles(baseMetricsSensor.getScala().getKey()))
        .thenReturn(FileTestUtils.getInputFiles("/baseMetricsSensor/", "ScalaFile", numberOfFiles));
    baseMetricsSensor.analyse(project, sensorContext);
  }
}