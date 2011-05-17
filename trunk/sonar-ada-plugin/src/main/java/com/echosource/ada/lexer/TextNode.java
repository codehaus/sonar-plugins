package com.echosource.ada.lexer;

import org.apache.commons.lang.StringUtils;

public class TextNode extends Node {

  public TextNode() {
    super(NodeType.Text);
  }

  public boolean isBlank() {
    return StringUtils.isBlank(getCode());
  }
}