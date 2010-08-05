/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web;

import static junit.framework.Assert.assertTrue;

import java.net.URISyntaxException;

import org.junit.Test;
import org.sonar.api.resources.Project;
import org.sonar.plugins.web.language.Web;

/**
 * @author Matthijs Galesloot
 */
public class TestWebSourceImporter extends AbstractWebPluginTester {

  @Test
  public void testImporter() throws URISyntaxException {

    final Project project = loadProjectFromPom();

    WebSourceImporter importer = new WebSourceImporter(Web.INSTANCE);

    assertTrue("Importer only supports web projects", importer.shouldExecuteOnProject(project));
    MockSensorContext sensorContext = new MockSensorContext();
    importer.analyse(project, sensorContext);
    assertTrue(sensorContext.getNumResources() > 0);
  }
}
