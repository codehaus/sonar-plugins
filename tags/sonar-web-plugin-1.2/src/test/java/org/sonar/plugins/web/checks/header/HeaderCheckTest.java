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
package org.sonar.plugins.web.checks.header;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.FileNotFoundException;
import java.io.StringReader;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Matthijs Galesloot
 */
public class HeaderCheckTest extends AbstractCheckTester {

  @Test
  public void validHeader() throws FileNotFoundException {

    String fragment = "<!-- Copyright the author and his friends --><h:someNode/>";

    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), HeaderCheck.class);

    assertThat(sourceCode.getViolations().size()).isEqualTo(0);
  }

  @Test
  public void missingHeader() throws FileNotFoundException {

    String fragment = "<h:someNode/>";

    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), HeaderCheck.class);

    assertThat(sourceCode.getViolations().size()).isEqualTo(1);
  }

  @Test
  public void wrongFormatHeader() throws FileNotFoundException {

    String fragment = "<!-- copyright is not spelled OK --><h:someNode/>";

    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), HeaderCheck.class);

    assertThat(sourceCode.getViolations().size()).isEqualTo(1);
  }
}
