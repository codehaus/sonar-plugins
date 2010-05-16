/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.visitor;

import java.util.ArrayList;
import java.util.List;

import org.sonar.plugins.web.node.CommentNode;
import org.sonar.plugins.web.node.DirectiveNode;
import org.sonar.plugins.web.node.ExpressionNode;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

public class PageScanner {

  private List<AbstractNodeVisitor> visitors = new ArrayList<AbstractNodeVisitor>();

  public void addVisitor(AbstractNodeVisitor visitor) {
    visitors.add(visitor);
  }

  public void scan(List<Node> nodeList, WebSourceCode webSourceCode) {

    // notify visitors for a new document
    for (AbstractNodeVisitor visitor : visitors) {
      visitor.startDocument(webSourceCode);
    }

    // notify the visitors for start and end of element
    for (Node node : nodeList) {
      for (AbstractNodeVisitor visitor : visitors) {
        scanElement(visitor, node);
      }
    }

    // notify visitors for end of document
    for (AbstractNodeVisitor visitor : visitors) {
      visitor.endDocument();
    }
  }

  private void scanElement(AbstractNodeVisitor visitor, Node node) {
    switch (node.getNodeType()) {
      case Tag:
        TagNode element = (TagNode) node;
        if ( !element.isEndElement()) {
          visitor.startElement(element);
        }
        if (element.isEndElement() || element.hasEnd()) {
          visitor.endElement(element);
        }
        break;
      case Text:
        visitor.characters((TextNode) node);
        break;
      case Comment:
        visitor.comment((CommentNode) node);
        break;
      case Expression:
        visitor.expression((ExpressionNode) node);
        break;
      case Directive:
        visitor.directive((DirectiveNode) node);
        break;
    }
  }

}
