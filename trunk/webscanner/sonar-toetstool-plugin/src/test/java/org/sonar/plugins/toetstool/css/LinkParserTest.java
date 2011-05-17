/*
 * Sonar Toetstool Plugin
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
package org.sonar.plugins.toetstool.css;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class LinkParserTest {

  private static final String testfile = "src/test/resources/org/sonar/plugins/toetstool/toetstool/a.html";

  @Test
  public void testLinkParser() throws FileNotFoundException {

    File file = new File(testfile);
    assertTrue(file.exists());
    List<String> styleSheets = new LinkParser().parseStylesheets(new FileInputStream(file));
    assertEquals(3, styleSheets.size());
  }

  @Test
  public void testLinkParserUnclosedTags() throws FileNotFoundException {

    String fragment = "<html><td><link type=\"text/css\" href=\"a.css\"><td></html>";
    List<String> styleSheets = new LinkParser().parseStylesheets(IOUtils.toInputStream(fragment));
    assertEquals(1, styleSheets.size());
  }

  @Test
  public void testLinkParserSingleQuotes() throws FileNotFoundException {

    String fragment = "<html><link type='text/css' href=\"a.css\"></html>";
    List<String> styleSheets = new LinkParser().parseStylesheets(IOUtils.toInputStream(fragment));
    assertEquals(1, styleSheets.size());
  }
}