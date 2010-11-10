/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.jacoco;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.doubleThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.Plugins;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.test.IsMeasure;

/**
 * @author Evgeny Mandrikov
 */
public class JaCoCoSensorTest {
  private JaCoCoSensor sensor;

  @Before
  public void setUp() {
    sensor = new JaCoCoSensor(mock(Plugins.class), mock(JaCoCoMavenPluginHandler.class));
  }

  @Test
  public void testSensorDefinition() {
    assertThat(sensor.toString(), is("JaCoCoSensor"));
  }

  @Test
  public void testGetMavenPluginHandler() {
    Project project = mock(Project.class);
    when(project.getAnalysisType()).thenReturn(Project.AnalysisType.REUSE_REPORTS).thenReturn(Project.AnalysisType.DYNAMIC);

    assertThat(sensor.getMavenPluginHandler(project), nullValue());
    assertThat(sensor.getMavenPluginHandler(project), instanceOf(JaCoCoMavenPluginHandler.class));
  }

  @Test
  public void testReadExecutionData() throws Exception {
    File jacocoExecutionData = new File(getClass().getResource("/org/sonar/plugins/jacoco/JaCoCoSensorTest/jacoco.exec").getFile());
    File buildOutputDir = jacocoExecutionData.getParentFile();
    SensorContext context = mock(SensorContext.class);

    final JavaFile resource = new JavaFile("org.sonar.plugins.jacoco.tests.Hello");
    when(context.getResource(any(Resource.class))).thenReturn(resource);

    new JaCoCoSensor.Analyzer().readExecutionData(jacocoExecutionData, buildOutputDir, context);

    verify(context).getResource(eq(resource));
    verify(context).saveMeasure(eq(resource), eq(CoreMetrics.LINES_TO_COVER), doubleThat(greaterThan(0d)));
    verify(context).saveMeasure(eq(resource), eq(CoreMetrics.UNCOVERED_LINES), doubleThat(greaterThan(0d)));
    verify(context).saveMeasure(eq(resource), argThat(new IsMeasure(CoreMetrics.COVERAGE_LINE_HITS_DATA)));
    verifyNoMoreInteractions(context);
  }

  @Test
  public void doNotSaveMeasureOnResourceWhichDoesntExistInTheContext() throws Exception {
    File jacocoExecutionData = new File(getClass().getResource("/org/sonar/plugins/jacoco/JaCoCoSensorTest/jacoco.exec").getFile());
    File buildOutputDir = jacocoExecutionData.getParentFile();
    SensorContext context = mock(SensorContext.class);
    when(context.getResource(any(Resource.class))).thenReturn(null);

    new JaCoCoSensor.Analyzer().readExecutionData(jacocoExecutionData, buildOutputDir, context);

    verify(context, never()).saveMeasure(any(Resource.class), any(Measure.class));
  }
}
