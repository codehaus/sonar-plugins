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

package org.sonar.plugins.jacoco.itcoverage;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.Plugins;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Resource;
import org.sonar.api.test.IsMeasure;
import org.sonar.api.test.IsResource;

import java.io.File;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.doubleThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class JaCoCoItSensorTest {
  private JaCoCoItSensor sensor;

  @Before
  public void setUp() {
    sensor = new JaCoCoItSensor(mock(Plugins.class));
  }

  @Test
  public void testSensorDefinition() {
    assertThat(sensor.toString(), is("JaCoCoItSensor"));
  }

  @Test
  public void testReadExecutionData() throws Exception {
    File jacocoExecutionData = new File(
        getClass().getResource("/org/sonar/plugins/jacoco/JaCoCoSensorTest/jacoco.exec").getFile()
        );
    File buildOutputDir = jacocoExecutionData.getParentFile();
    SensorContext context = mock(SensorContext.class);

    sensor.readExecutionData(jacocoExecutionData, buildOutputDir, context);

    verify(context).saveMeasure(
        argThat(new IsResource(Resource.SCOPE_ENTITY, Resource.QUALIFIER_CLASS, "org.sonar.plugins.jacoco.tests.Hello")),
        eq(JaCoCoItMetrics.IT_LINES_TO_COVER),
        doubleThat(greaterThan(0d))
      );
    verify(context).saveMeasure(
        argThat(new IsResource(Resource.SCOPE_ENTITY, Resource.QUALIFIER_CLASS, "org.sonar.plugins.jacoco.tests.Hello")),
        eq(JaCoCoItMetrics.IT_UNCOVERED_LINES),
        doubleThat(greaterThan(0d))
      );
    verify(context).saveMeasure(
        argThat(new IsResource(Resource.SCOPE_ENTITY, Resource.QUALIFIER_CLASS, "org.sonar.plugins.jacoco.tests.Hello")),
        argThat(new IsMeasure(JaCoCoItMetrics.IT_COVERAGE_LINE_HITS_DATA))
      );
    verifyNoMoreInteractions(context);
  }

}
