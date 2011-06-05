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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class CodeSizePluginTest {

  @Test
  public void codeSizePlugin() {
    CodeSizePlugin codeSizePlugin = new CodeSizePlugin();
    assertNull(codeSizePlugin.getKey());
    assertNull(codeSizePlugin.getName());
    assertNull(codeSizePlugin.getDescription());
    assertNotNull(codeSizePlugin.toString());
    assertEquals(4, codeSizePlugin.getExtensions().size());
  }

  @Test
  public void testMetrics() {
    assertEquals(1, new CodesizeMetrics().getMetrics().size());
  }

  @Test
  public void testDashboard() {
    DashboardWidget widget = new DashboardWidget();
    assertNotNull(widget.getTemplate());
    assertNotNull(widget.getTitle());
    assertEquals("codesize", widget.getId());
  }
}
