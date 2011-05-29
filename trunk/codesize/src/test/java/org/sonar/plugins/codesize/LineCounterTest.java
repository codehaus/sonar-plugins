/*
 * Codesize
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

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.sonar.plugins.codesize.xml.SizingMetric;

public class LineCounterTest {

  @Test
  public void testLineCounter() {
    LineCounter lineCounter = new LineCounter();
    lineCounter.setDefaultCharset(Charset.defaultCharset());
    SizingMetrics sizingMetrics = new SizingMetrics(new PropertiesConfiguration());
    for (SizingMetric sizingMetric : sizingMetrics.getSizingMetrics()) {
      int lines = lineCounter.calculateLinesOfCode(new File("."), sizingMetric);

      String[] withLines = new String[] { "HTML", "Java", "XML", "Test" };
      if (ArrayUtils.contains(withLines, sizingMetric.getName())) {
        assertTrue(sizingMetric.getName(), lines > 0);
      } else {
        assertEquals(sizingMetric.getName(), 0, lines);
      }
    }
  }
}
