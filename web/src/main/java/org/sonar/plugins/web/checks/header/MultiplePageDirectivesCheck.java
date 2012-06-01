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

package org.sonar.plugins.web.checks.header;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.visitor.WebSourceCode;

/**
 * Checker to find multiple page directives, where 1 page directive would be preferred.
 *
 * @see http://java.sun.com/developer/technicalArticles/javaserverpages/code_convention/ paragraph JSP Page Directive(s)
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "MultiplePageDirectivesCheck", priority = Priority.MINOR)
public class MultiplePageDirectivesCheck extends AbstractPageCheck {

  private static boolean isImportDirective(DirectiveNode node) {
    return node.getAttributes().size() == 1 && node.getAttribute("import") != null;
  }

  private DirectiveNode node;

  private int pageDirectives;

  @Override
  public void directive(DirectiveNode node) {
    if (!node.isHtml() && "page".equalsIgnoreCase(node.getNodeName()) && !isImportDirective(node)) {
      pageDirectives++;
      this.node = node;
    }
  }

  @Override
  public void endDocument() {
    if (pageDirectives > 1) {
      createViolation(node.getStartLinePosition(), "Avoid multiple page directives.");
    }
  }

  @Override
  public void startDocument(WebSourceCode webSourceCode) {
    super.startDocument(webSourceCode);
    pageDirectives = 0;
  }
}
