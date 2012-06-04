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
package org.sonar.plugins.web.checks.attributes;

import org.junit.Test;
import org.sonar.plugins.web.checks.AbstractCheckTester;
import org.sonar.plugins.web.visitor.WebSourceCode;

import java.io.StringReader;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * @author Matthijs Galesloot
 */
public class IllegalAttributeCheckTest extends AbstractCheckTester {

  @Test
  public void validateCheckParameters() {
    String params = "a:a,b,c";
    IllegalAttributeCheck check = (IllegalAttributeCheck) instantiateCheck(IllegalAttributeCheck.class,
        "attributes", params);
    assertEquals(params, check.getAttributes());
  }

  @Test
  public void testAttributeCheck() {

    String fragment = "<h:someNode class=\"redflag\"/>";

    WebSourceCode sourceCode = parseAndCheck(new StringReader(fragment), IllegalAttributeCheck.class, "attributes", "class");

    assertThat(sourceCode.getViolations().size()).isEqualTo(1);
  }
}
