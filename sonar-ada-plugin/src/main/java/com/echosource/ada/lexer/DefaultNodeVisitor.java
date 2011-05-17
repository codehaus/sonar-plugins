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

package com.echosource.ada.lexer;

import java.util.List;

/**
 * Defines interface for node visitor with default dummy implementations.
 * 
 * @author Matthijs Galesloot
 */
public class DefaultNodeVisitor implements NodeVisitor {

  private AdaSourceCode sourceCode;

  public void characters(TextNode textNode) {

  }

  public void comment(CommentNode node) {

  }

  public void directive(DirectiveNode node) {

  }

  public void endDocument() {

  }

  public void endElement(TagNode node) {

  }

  public void expression(ExpressionNode node) {

  }

  public AdaSourceCode getAdaSourceCode() {
    return sourceCode;
  }

  public void startDocument(AdaSourceCode sourceCode, List<Node> nodes) {
    startDocument(sourceCode);
  }

  public void startDocument(AdaSourceCode sourceCode) {
    this.sourceCode = sourceCode;
  }

  public void startElement(TagNode node) {

  }
}