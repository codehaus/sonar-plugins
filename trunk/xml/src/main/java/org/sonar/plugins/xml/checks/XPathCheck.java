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

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.PrefixResolverDefault;
import org.sonar.api.utils.SonarException;
import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.xml.parsers.SaxParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@Rule(key = "XPathCheck", name = "XPath Check", description = "XPath Check", priority = Priority.CRITICAL,
    isoCategory = IsoCategory.Reliability)
public class XPathCheck extends AbstractPageCheck {

  private final class DocumentNamespaceContext implements NamespaceContext {

    private final PrefixResolver resolver;

    private DocumentNamespaceContext(PrefixResolver resolver) {
      this.resolver = resolver;
    }

    public String getNamespaceURI(String prefix) {
        String namespace = resolver.getNamespaceForPrefix(prefix);
        return namespace;
    }

    // Dummy implementation - not used!
    public Iterator getPrefixes(String val) {
        return null;
    }

    // Dummy implemenation - not used!
    public String getPrefix(String uri) {
        return null;
    }
  }

  @RuleProperty(key = "expression")
  private String expression;

  private void evaluateXPath() {

    Document document = getWebSourceCode().getDocument(expression.contains(":"));

    try {
      NodeList nodes = (NodeList) getXPathExpressionForDocument(document).evaluate(document, XPathConstants.NODESET);
      for (int i = 0; i < nodes.getLength(); i++) {

        int lineNumber = SaxParser.getLineNumber(nodes.item(i));
        createViolation(lineNumber);
      }
    } catch (XPathExpressionException e) {
      throw new SonarException(e);
    }
  }

  public String getExpression() {
    return expression;
  }

  private XPathExpression getXPathExpressionForDocument(Document document) {
    if (expression != null) {
      try {
        XPath xpath = XPathFactory.newInstance().newXPath();
        PrefixResolver resolver = new PrefixResolverDefault(document.getDocumentElement());
        xpath.setNamespaceContext(new DocumentNamespaceContext(resolver));
        return xpath.compile(expression);
      } catch (XPathExpressionException e) {
        throw new SonarException(e);
      }
    }
    return null;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  @Override
  public void validate(XmlSourceCode xmlSourceCode) {
    setWebSourceCode(xmlSourceCode);

    if (expression != null) {
      evaluateXPath();
    }
  }
}
