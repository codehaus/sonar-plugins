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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.sonar.plugins.web.markup.validation.CharsetDetector;

public class CharsetTest {

  private static final String path = "src/test/resources/org/sonar/plugins/web/encoding";

  @Test
  public void testCharset() throws IOException {

    String charset;
    charset = CharsetDetector.detect(new File(path + "/UTF8-BOM.html"));
    assertEquals("UTF-8", charset);

    charset = CharsetDetector.detect(new File(path + "/UTF8-withoutBOM.html"));
    assertEquals("UTF-8", charset);

    charset = CharsetDetector.detect(new File(path + "/ANSI.html"));
    assertEquals("UTF-8", charset);

    charset = CharsetDetector.detect(new File(path + "/Meta.html"));
    assertEquals("UTF-16", charset);
  }
}
