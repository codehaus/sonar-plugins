/*
 * Sonar JaCoCo Plugin
 * Copyright (C) 2010 SonarSource
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

package org.sonar.plugins.jacoco.itcoverage;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.doubleThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.io.File;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Project.AnalysisType;
import org.sonar.api.resources.Resource;
import org.sonar.api.test.IsMeasure;
import org.sonar.plugins.jacoco.JacocoConfiguration;

public class JaCoCoItSensorTest {
  private JacocoConfiguration configuration;
  private JaCoCoItSensor sensor;

  @Before
  public void setUp() {
    configuration = mock(JacocoConfiguration.class);
    sensor = new JaCoCoItSensor(configuration);
  }

  @Test
  public void testSensorDefinition() {
    assertThat(sensor.toString(), is("JaCoCoItSensor"));
  }

  @Test
  public void doNotExecuteWhenReportPathNotSpecified() {
    when(configuration.getItReportPath()).thenReturn("");
    Project project = mock(Project.class);
    assertThat(sensor.shouldExecuteOnProject(project), is(false));
  }

  @Test
  public void shouldExecuteOnProject() {
    when(configuration.getItReportPath()).thenReturn("target/it-jacoco.exec");
    Project project = mock(Project.class);
    when(project.getAnalysisType()).thenReturn(AnalysisType.DYNAMIC).thenReturn(AnalysisType.REUSE_REPORTS);
    assertThat(sensor.shouldExecuteOnProject(project), is(true));
    assertThat(sensor.shouldExecuteOnProject(project), is(true));
  }

  @Ignore
  @Test
  public void testReadExecutionData() throws Exception {
    File jacocoExecutionData = new File(getClass().getResource("/org/sonar/plugins/jacoco/JaCoCoSensorTest/jacoco.exec").getFile());
    File buildOutputDir = jacocoExecutionData.getParentFile();
    SensorContext context = mock(SensorContext.class);

    final JavaFile resource = new JavaFile("org.sonar.plugins.jacoco.tests.Hello");
    when(context.getResource(any(Resource.class))).thenReturn(resource);

    // new JaCoCoItSensor.Analyzer().readExecutionData(jacocoExecutionData, buildOutputDir, context);

    verify(context).getResource(eq(resource));
    verify(context).saveMeasure(eq(resource), eq(JaCoCoItMetrics.IT_LINES_TO_COVER), doubleThat(greaterThan(0d)));
    verify(context).saveMeasure(eq(resource), eq(JaCoCoItMetrics.IT_UNCOVERED_LINES), doubleThat(greaterThan(0d)));
    verify(context).saveMeasure(eq(resource), argThat(new IsMeasure(JaCoCoItMetrics.IT_COVERAGE_LINE_HITS_DATA)));
    verify(context).saveMeasure(eq(resource), eq(JaCoCoItMetrics.IT_CONDITIONS_TO_COVER), doubleThat(greaterThan(0d)));
    verify(context).saveMeasure(eq(resource), eq(JaCoCoItMetrics.IT_UNCOVERED_CONDITIONS), doubleThat(greaterThan(0d)));
    verify(context).saveMeasure(eq(resource), argThat(new IsMeasure(JaCoCoItMetrics.IT_BRANCH_COVERAGE_HITS_DATA)));
    verifyNoMoreInteractions(context);
  }

  @Ignore
  @Test
  public void doNotSaveMeasureOnResourceWhichDoesntExistInTheContext() throws Exception {
    File jacocoExecutionData = new File(getClass().getResource("/org/sonar/plugins/jacoco/JaCoCoSensorTest/jacoco.exec").getFile());
    File buildOutputDir = jacocoExecutionData.getParentFile();
    SensorContext context = mock(SensorContext.class);
    when(context.getResource(any(Resource.class))).thenReturn(null);

    // new JaCoCoItSensor.Analyzer().readExecutionData(jacocoExecutionData, buildOutputDir, context);

    verify(context, never()).saveMeasure(any(Resource.class), any(Measure.class));
  }
}
