/*
 * Sonar Web Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package org.sonar.plugins.web.core;

import org.sonar.plugins.web.AbstractWebPluginTester;

import org.sonar.plugins.web.core.WebSourceImporter;

import org.sonar.plugins.web.core.Web;

import org.junit.Test;
import org.sonar.api.config.Settings;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Matthijs Galesloot
 */
public class WebSourceImporterTest extends AbstractWebPluginTester {

  @Test
  public void testToString() throws Exception {
    assertThat(new WebSourceImporter(new Web(new Settings())).toString()).isEqualTo("Web Source Importer");
  }
}
