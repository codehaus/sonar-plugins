package com.echosource.ada.lexer;

/**

 */
public class CommentNode extends Node {

  private boolean html;

  public CommentNode() {
    super(NodeType.Comment);
  }

  public boolean isHtml() {
    return html;
  }

  public void setHtml(boolean html) {
    this.html = html;
  }

}