/*
 * Sonar XML Plugin
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

package org.sonar.plugins.xml.checks;

import org.apache.commons.lang.StringUtils;
import org.sonar.check.Cardinality;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.xml.parsers.SaxParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Perform check for tab.
 * 
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Rule(key = "IllegalTabCheck", name = "Illegal Tab Check", description = "Tab Character", priority = Priority.MINOR,
    cardinality = Cardinality.SINGLE)
public class IllegalTabCheck extends AbstractPageCheck {

  @RuleProperty(key = "markAll", description = "Mark All Tab Errors", defaultValue = "false")
  private boolean markAll;

  private boolean validationReady;

  /**
   * Find Illegal tabs in whitespace.
   */
  private void findIllegalTabs(Node node) {

    // check whitespace in the node
    for (Node sibling = node.getPreviousSibling(); sibling != null; sibling = sibling.getPreviousSibling()) {
      if (sibling.getNodeType() == Node.TEXT_NODE) {
        String text = sibling.getTextContent();
        if (StringUtils.isWhitespace(text) && StringUtils.contains(text, "\t")) {
          createNewViolation(SaxParser.getLineNumber(sibling));
          break; // one violation for this node is enough
        }
      }
    }

    // check the child elements of the node
    for (Node child = node.getFirstChild(); !validationReady && child != null; child = child.getNextSibling()) {
      switch (child.getNodeType()) {
        case Node.ELEMENT_NODE:
          findIllegalTabs(child);
          break;
        default:
          break;
      }
    }
  }

  private void createNewViolation(int lineNumber) {
    if (!markAll) {
      createViolation(lineNumber, "Tab characters found (this is the first occurrence)");
      validationReady = true;
    } else {
      createViolation(lineNumber);
    }
  }

  @Override
  public void validate(XmlSourceCode xmlSourceCode) {
    setWebSourceCode(xmlSourceCode);

    validationReady = false;
    Document document = getWebSourceCode().getDocument(false);
    if (document != null && document.getDocumentElement() != null) {
      findIllegalTabs(document.getDocumentElement());
    }
  }

  
  public boolean isMarkAll() {
    return markAll;
  }

  
  public void setMarkAll(boolean markAll) {
    this.markAll = markAll;
  }
}
