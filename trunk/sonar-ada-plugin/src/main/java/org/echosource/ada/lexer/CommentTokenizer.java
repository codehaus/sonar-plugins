package com.echosource.ada.lexer;

import java.util.List;

class CommentTokenizer extends AbstractTokenizer<List<Node>> {

  private final Boolean html;

  public CommentTokenizer(String startToken, String endToken, Boolean html) {
    super(startToken, endToken);

    this.html = html;
  }

  @Override
  Node createNode() {

    CommentNode node = new CommentNode();
    node.setHtml(html);
    return node;
  }
}