/*
 * Ada Sonar Plugin
 * Copyright (C) 2010 Akram Ben Aissi
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.ada.lexer;

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