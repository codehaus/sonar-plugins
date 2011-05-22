/*
 * Sonar Web Plugin
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

package org.sonar.plugins.web.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.sonar.api.resources.Project;
import org.sonar.plugins.web.ProjectConfiguration;
import org.sonar.plugins.web.api.ProjectFileManager;

/**
 * @author Matthijs Galesloot
 */
public class WebTest {

  @Test
  public void testFileSuffixes() {
    Project project = new Project("test");
    PropertiesConfiguration configuration = new PropertiesConfiguration();
    project.setConfiguration(configuration);
    ProjectFileManager projectFileManager = new ProjectFileManager(project);
    assertEquals(3, projectFileManager.getFileSuffixes().length);

    configuration.setProperty(ProjectConfiguration.FILE_EXTENSIONS, "one,two");
    assertEquals(2, projectFileManager.getFileSuffixes().length);

    configuration.setProperty(ProjectConfiguration.FILE_EXTENSIONS, "one");
    assertEquals(1, projectFileManager.getFileSuffixes().length);

    configuration.setProperty(ProjectConfiguration.FILE_EXTENSIONS, "");
    assertEquals(3, projectFileManager.getFileSuffixes().length);
  }

  @Test
  public void testWebFootprint() {
    WebFootprint footprint = new WebFootprint();
    assertNotNull(footprint.getDetectors());
  }
}
