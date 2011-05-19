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

package org.sonar.plugins.ada.core;

import org.sonar.channel.CodeReader;
import org.sonar.channel.EndMatcher;
import org.sonar.colorizer.HtmlCodeBuilder;
import org.sonar.colorizer.LiteralTokenizer;

/**
 * An Ada litteral starts with ", and does not allow escape chars. Special chars are treated through a special class in Ada, and saved using
 * their ascii code. The single quote ' is used to delimit only char. And it is also used to access attributes. For example, <pre>String'Length</pre> or
 * <pre>Integer'Value</pre>.
 * 
 * @author Akram Ben Aissi
 * 
 */
public class AdaLiteralTokenizer extends LiteralTokenizer {

  private final String tagBefore;
  private final String tagAfter;

  public AdaLiteralTokenizer(String tagBefore, String tagAfter) {
    this.tagBefore = tagBefore;
    this.tagAfter = tagAfter;
  }

  public AdaLiteralTokenizer() {
    this("", "");
  }

  @Override
  /**
   * Ada strings 
   */
  public boolean consume(CodeReader code, HtmlCodeBuilder codeBuilder) {
    int peek = code.peek();
    if (peek == '\"' || peek == '\'') {
      EndMatcher matcher = new EndCommentMatcher(peek, code);
      if (peek == '\'') {
        matcher = new EndCharMatcher(code);
      }
      codeBuilder.appendWithoutTransforming(tagBefore);
      code.popTo(matcher, codeBuilder);
      codeBuilder.appendWithoutTransforming(tagAfter);
      return true;
    }
    return false;
  }

  private static class EndCommentMatcher implements EndMatcher {

    private final int firstChar;
    private final CodeReader code;
    private StringBuilder literalValue;

    public EndCommentMatcher(int firstChar, CodeReader code) {
      this.firstChar = firstChar;
      this.code = code;
      literalValue = new StringBuilder();
    }

    public boolean match(int endFlag) {
      literalValue.append((char) endFlag);
      return (code.lastChar() == firstChar && literalValue.length() > 1);
    }
  }

  private static class EndCharMatcher implements EndMatcher {

    private final CodeReader code;

    public EndCharMatcher(CodeReader code) {
      this.code = code;
    }

    public boolean match(int endFlag) {
      return code.peek(2)[1] != '\'';
    }
  }
}
