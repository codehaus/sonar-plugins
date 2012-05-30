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

package org.sonar.plugins.web.core;

import org.sonar.plugins.web.core.Web;
import org.sonar.plugins.web.core.WebFootprint;

import org.junit.Test;
import org.sonar.api.config.Settings;
import org.sonar.plugins.web.api.WebConstants;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

/**
 * @author Matthijs Galesloot
 */
public class WebTest {

  @Test
  public void testDefaultFileSuffixes() {
    Web web = new Web(new Settings());
    assertThat(web.getFileSuffixes()).containsOnly("xhtml", "jspf", "jsp");
  }

  @Test
  public void testCustomFileSuffixes() {
    Settings settings = new Settings();
    settings.appendProperty(WebConstants.FILE_EXTENSIONS_PROP_KEY, "foo, bar ,   toto");
    Web web = new Web(settings);
    assertThat(web.getFileSuffixes()).containsOnly("foo", "bar", "toto");
  }

  @Test
  public void testWebFootprint() {
    WebFootprint footprint = new WebFootprint();
    assertNotNull(footprint.getDetectors());
  }
}
