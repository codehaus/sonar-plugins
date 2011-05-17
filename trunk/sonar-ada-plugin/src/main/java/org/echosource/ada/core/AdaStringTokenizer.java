package com.echosource.ada.core;

import org.sonar.channel.CodeReader;
import org.sonar.colorizer.HtmlCodeBuilder;
import org.sonar.colorizer.LiteralTokenizer;
import org.sonar.colorizer.Tokenizer;

public class AdaStringTokenizer extends Tokenizer {

  private final LiteralTokenizer tokenizer;

  public AdaStringTokenizer(String tagBefore, String tagAfter) {
    tokenizer = new AdaLiteralTokenizer(tagBefore, tagAfter);
  }

  public AdaStringTokenizer() {
    tokenizer = new AdaLiteralTokenizer("", "");
  }

  @Override
  public boolean consume(CodeReader code, HtmlCodeBuilder output) {
    return tokenizer.consume(code, output);
  }
}
