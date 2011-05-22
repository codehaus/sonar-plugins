/*
 * Sonar W3C Markup Validation Plugin
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
package org.sonar.plugins.web.markup;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matthijs Galesloot
 */
public class PageCountLinesTest {

  private static final Logger LOG = LoggerFactory.getLogger(PageCountLinesTest.class);

  @Test
  public void testCountLines() throws FileNotFoundException {

//    String fileName = "src/test/resources/src/main/webapp/user-properties.jsp";
//    new LineCountSensor().analyse(project, new MockSensorContext());
//
//    LOG.warn("Lines:" + webSourceCode.getMeasure(CoreMetrics.LINES).getIntValue());
//
//    int numLines = 287;
//    assertTrue("Expected " + numLines + " lines, but was: " + webSourceCode.getMeasure(CoreMetrics.LINES).getIntValue(), webSourceCode
//        .getMeasure(CoreMetrics.LINES).getIntValue() == numLines);
  }
}
