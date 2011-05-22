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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.sonar.api.resources.Project;

/**
 * @author Matthijs Galesloot
 */
public class WebScannerSensorTest {

  @Test
  public void webScannerPlugin() {
    WebScannerPlugin webscannerPlugin = new WebScannerPlugin();
    assertNull(webscannerPlugin.getKey());
    assertNull(webscannerPlugin.getName());
    assertNull(webscannerPlugin.getDescription());
    assertNotNull(webscannerPlugin.toString());
    assertEquals(1, webscannerPlugin.getExtensions().size());
  }

  @Test
  public void webScanner() {
    WebScanner scanner = new WebScanner(null);
    assertNotNull(scanner.toString());

    Project project = new Project("test");
    assertFalse(scanner.shouldExecuteOnProject(project));

    project.setLanguageKey("web");
    assertFalse(scanner.shouldExecuteOnProject(project));

    project.setConfiguration(new PropertiesConfiguration());
    project.getConfiguration().addProperty(WebScannerPlugin.WEBSITE, "test");
    assertTrue(scanner.shouldExecuteOnProject(project));
  }
}
