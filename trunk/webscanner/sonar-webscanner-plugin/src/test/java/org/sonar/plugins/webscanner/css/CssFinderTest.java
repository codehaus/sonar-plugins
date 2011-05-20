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

package org.sonar.plugins.webscanner.css;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.junit.Test;
import org.sonar.plugins.webscanner.crawler.parser.LinkExtractor;

public class CssFinderTest {

  private static final String path = "src/test/resources/org/sonar/plugins/webscanner/css";

  private static final String testfile = path + "/web/stylesheet-includes.html";

  @Test
  public void testCssFinder() throws FileNotFoundException {

    File file = new File(testfile);
    assertTrue(file.exists());
    LinkExtractor extractor = new LinkExtractor();
    extractor.parseLinks(new FileReader(file));
    List<String> styleSheets = extractor.getUrls();
    assertEquals(3, styleSheets.size());

    File rootDir = new File(path);
    CssFinder cssFinder = new CssFinder();
    File[] cssFiles = cssFinder.findCssFiles(styleSheets, file, rootDir);
    assertEquals(styleSheets.size(), cssFiles.length);
    File[] importedFiles = cssFinder.findCssImports(cssFiles, rootDir);
    assertEquals(styleSheets.size(), importedFiles.length);
  }
}