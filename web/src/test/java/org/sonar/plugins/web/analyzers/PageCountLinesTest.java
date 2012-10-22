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
package org.sonar.plugins.web.analyzers;

import org.junit.Test;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.File;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.visitor.PageScanner;
import org.sonar.plugins.web.visitor.WebSourceCode;
import org.sonar.test.TestUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Matthijs Galesloot
 */
public class PageCountLinesTest {

  @Test
  public void testCountLines() throws FileNotFoundException {
    PageLexer lexer = new PageLexer();
    List<Node> nodeList = lexer.parse(new FileReader(TestUtils.getResource("src/main/webapp/user-properties.jsp")));
    assertTrue(nodeList.size() > 100);

    File webFile = new File("test", "user-properties.jsp");

    final PageScanner scanner = new PageScanner();
    scanner.addVisitor(new PageCountLines());
    WebSourceCode webSourceCode = new WebSourceCode(webFile);
    scanner.scan(nodeList, webSourceCode);

    assertThat(webSourceCode.getMeasure(CoreMetrics.LINES).getIntValue()).isEqualTo(287);
    assertThat(webSourceCode.getMeasure(CoreMetrics.NCLOC).getIntValue()).isEqualTo(227);
    assertThat(webSourceCode.getMeasure(CoreMetrics.COMMENT_LINES).getIntValue()).isEqualTo(14);
  }
}
