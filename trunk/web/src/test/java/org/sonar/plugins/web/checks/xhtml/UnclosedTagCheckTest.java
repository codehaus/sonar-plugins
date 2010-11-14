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

package org.sonar.plugins.web.checks.xhtml;

import static junit.framework.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * @author Matthijs Galesloot
 */
public class UnclosedTagCheckTest extends AbstractCheckTester {

  @Test
  public void testUnclosedTagCheck() throws FileNotFoundException {

    String fragment = "<td><br><tr>";

    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), UnclosedTagCheck.class);

    int numViolations = 3;

    assertTrue("Should have found " + numViolations + " violations", sourceCode.getViolations().size() == numViolations);
  }

  @Test
  public void testClosedNestedTagCheck() throws IOException {
    FileReader reader = new FileReader("src/test/resources/checks/unclosedtag.html");
    WebSourceCode sourceCode = parseAndCheck(reader, UnclosedTagCheck.class);

    int numViolations = 0;
    assertTrue("Should have found " + numViolations + " violations", sourceCode.getViolations().size() == numViolations);
  }
}
