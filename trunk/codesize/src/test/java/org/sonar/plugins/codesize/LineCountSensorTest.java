/*
 * Sonar Codesize Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.codesize;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;

public class LineCountSensorTest {

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);

    Mockito.when(projectFileSystem.getSourceCharset()).thenReturn(Charset.defaultCharset());
    Mockito.when(projectFileSystem.getBasedir()).thenReturn(new File("."));
  }

  @Mock
  private SensorContext sensorContext;

  @Mock
  private ProjectFileSystem projectFileSystem;

  @Test
  public void testSensor() {
    Project project = new Project("test");
    project.setConfiguration(new PropertiesConfiguration());
    project.getConfiguration().setProperty(CodesizeConstants.SONAR_CODESIZE_ACTIVE, "yes");
    project.setFileSystem(projectFileSystem);

    LineCountSensor sensor = new LineCountSensor(new PropertiesConfiguration());
    assertNotNull(sensor.toString());
    assertTrue(sensor.shouldExecuteOnProject(project));
    sensor.analyse(project, sensorContext);
  }
}
