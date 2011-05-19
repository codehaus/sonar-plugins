/*
 * Sonar Webscanner Plugin
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
package org.sonar.plugins.webscanner;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.Project;
import org.sonar.plugins.webscanner.markup.W3CMarkupSensor;
import org.sonatype.aether.RepositorySystemSession;

/**
 * @author Matthijs Galesloot
 */
public class WebScannerSensorTest extends AbstractWebScannerPluginTester {

  private static final Logger LOG = LoggerFactory.getLogger(WebScannerSensorTest.class);

  @Test
  public void webScannerPlugin() {
    WebScannerPlugin webscannerPlugin = new WebScannerPlugin();
    assertNull(webscannerPlugin.getKey());
    assertNull(webscannerPlugin.getName());
    assertNull(webscannerPlugin.getDescription());
    assertEquals(8, webscannerPlugin.getExtensions().size());
  }

  private class MockMavenSession extends MavenSession {
    public MockMavenSession() {
      super(null, (RepositorySystemSession) null, new DefaultMavenExecutionRequest(), null);
    }
  }

  @Test
  public void testSensor() throws Exception {
    W3CMarkupSensor sensor = new W3CMarkupSensor(new MockMavenSession(), createStandardRulesProfile(), new MarkupRuleFinder());

    File pomFile = new File(WebScannerSensorTest.class.getResource("/pom.xml").toURI());

    final Project project = loadProjectFromPom(pomFile);

    assertTrue(sensor.shouldExecuteOnProject(project));

    MockSensorContext sensorContext = new MockSensorContext();
    sensor.analyse(project, sensorContext);

    assertTrue("Should have found 1 violation", sensorContext.getViolations().size() > 0);
  }
}
