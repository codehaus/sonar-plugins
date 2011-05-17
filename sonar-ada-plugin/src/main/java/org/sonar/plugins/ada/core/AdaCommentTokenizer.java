package com.echosource.ada.core;

import org.sonar.colorizer.InlineDocTokenizer;

public class AdaCommentTokenizer extends InlineDocTokenizer {

  public AdaCommentTokenizer(String tagBefore, String tagAfter) {
    super("--", tagBefore, tagAfter);
  }

}
