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

package org.sonar.plugins.web.lex;

import org.sonar.channel.ChannelDispatcher;
import org.sonar.channel.CodeReader;
import org.sonar.plugins.web.node.Node;
import org.sonar.plugins.web.node.NodeType;
import org.sonar.plugins.web.node.TagNode;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Lexical analysis of a web page.
 *
 * @author Matthijs Galesloot
 */
@SuppressWarnings("unchecked")
public final class PageLexer {

  /**
   * The order of the tokenizers is significant, as they are processed in this order.
   *
   * TextTokenizer must be last, it will always consume the characters until the next token arrives.
   */
  @SuppressWarnings("rawtypes")
  private static List tokenizers = Arrays.asList(
      /* HTML Comments */
      new CommentTokenizer("<!--", "-->", true),
      /* JSP Comments */
      new CommentTokenizer("<%--", "--%>", false),
      /* HTML Directive */
      new DoctypeTokenizer("<!DOCTYPE", ">"),
      /* XML Directives */
      new DirectiveTokenizer("<?", "?>"),
      /* JSP Directives */
      new DirectiveTokenizer("<%@", "%>"),
      /* JSP Expressions */
      new ExpressionTokenizer("<%", "%>"),
      /* XML and HTML Tags */
      new ElementTokenizer("<", ">"),
      /* Text (for everything else) */
      new TextTokenizer());

  /**
   * Parse a nested node.
   */
  @SuppressWarnings("rawtypes")
  public List<Node> nestedParse(CodeReader reader) {
    List<Node> nodeList = new ArrayList<Node>();
    for (AbstractTokenizer tokenizer : (List<AbstractTokenizer>) tokenizers) {
      if (tokenizer.consume(reader, nodeList)) {
        break;
      }
    }
    return nodeList;
  }

  /**
   * Parse the input into a list of tokens, with parent/child relations between the tokens.
   */
  public List<Node> parse(Reader reader) {

    // CodeReader reads the file stream
    CodeReader codeReader = new CodeReader(reader);

    // ArrayList collects the nodes
    List<Node> nodeList = new ArrayList<Node>();

    // ChannelDispatcher manages the tokenizers
    ChannelDispatcher<List<Node>> channelDispatcher = new ChannelDispatcher<List<Node>>(tokenizers);
    channelDispatcher.consume(codeReader, nodeList);

    createNodeHierarchy(nodeList);

    // clean up
    codeReader.close();

    return nodeList;
  }

  /**
   * Scan the nodes and build the hierarchy of parent and child nodes.
   */
  private void createNodeHierarchy(List<Node> nodeList) {
    TagNode current = null;
    for (Node node : nodeList) {
      if (node.getNodeType() == NodeType.Tag) {
        TagNode element = (TagNode) node;

        // start element
        if (!element.isEndElement()) {
          element.setParent(current);
          current = element;
        }

        // end element
        if ((element.isEndElement() || element.hasEnd()) && current != null) {
          current = current.getParent();
        }
      }
    }
  }
}
