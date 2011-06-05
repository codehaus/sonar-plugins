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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

public class LineCounterTest {

  @Test
  public void testLineCounter() {
    LineCounter lineCounter = new LineCounter();
    lineCounter.setDefaultCharset(Charset.defaultCharset());
    SizingProfile profile = new SizingProfile(new PropertiesConfiguration());
    for (FileSetDefinition fileSetDefinition : profile.getFileSetDefinitions()) {
      int lines = lineCounter.calculateLinesOfCode(new File("."), fileSetDefinition);

      String[] withLines = new String[] { "HTML", "Java", "XML", "Test" };
      if (ArrayUtils.contains(withLines, fileSetDefinition.getName())) {
        assertTrue(fileSetDefinition.getName(), lines > 0);
      } else {
        assertEquals(fileSetDefinition.getName(), 0, lines);
      }
    }
  }

  @Test
  public void customProfile() {
    LineCounter lineCounter = new LineCounter();
    Configuration configuration = new PropertiesConfiguration();
    configuration.setProperty(CodesizeConstants.SONAR_CODESIZE_PROFILE, "Java\nincludes=src/main/java/**/*.java\nexcludes=src/main/java/**/*.java");
    SizingProfile profile = new SizingProfile(configuration);
    int lines = lineCounter.calculateLinesOfCode(new File("."), profile.getFileSetDefinitions().get(0));
    assertEquals(0, lines);
  }
}
