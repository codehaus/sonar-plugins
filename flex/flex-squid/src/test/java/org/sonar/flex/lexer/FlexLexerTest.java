/*
 * Sonar Flex Plugin
 * Copyright (C) 2010 SonarSource
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
package org.sonar.flex.lexer;

import com.sonar.sslr.impl.Lexer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.flex.api.FlexTokenType;

import static com.sonar.sslr.test.lexer.LexerMatchers.hasComment;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;
import static org.junit.Assert.assertThat;

public class FlexLexerTest {

  private static Lexer lexer;

  @BeforeClass
  public static void init() {
    lexer = FlexLexer.create();
  }

  @Test
  public void regular_expression_literal() throws Exception {
    assertThat("simple", lexer.lex("/a/"), hasToken("/a/", FlexTokenType.REGULAR_EXPRESSION_LITERAL));
    assertThat("flags", lexer.lex("/a/g"), hasToken("/a/g", FlexTokenType.REGULAR_EXPRESSION_LITERAL));
    assertThat("escaped slash", lexer.lex("/\\/a/"), hasToken("/\\/a/", FlexTokenType.REGULAR_EXPRESSION_LITERAL));
    // TODO
    // assertThat("ambiguation", lexer.lex("1 / a == 1 / b"), hasTokens("1", "/", "a", "==", "1", "/", "b", "EOF"));
  }

  @Test
  public void multiline_comment() {
    assertThat(lexer.lex("/* My Comment \n*/"), hasComment("/* My Comment \n*/"));
    assertThat(lexer.lex("/**/"), hasComment("/**/"));
  }

  @Test
  public void inline_comment() {
    assertThat(lexer.lex("// My Comment \n new line"), hasComment("// My Comment "));
    assertThat(lexer.lex("//"), hasComment("//"));
  }

  @Test
  public void decimal_literal() {
    assertThat(lexer.lex("0"), hasToken("0", FlexTokenType.NUMERIC_LITERAL));
    assertThat(lexer.lex("1239"), hasToken("1239", FlexTokenType.NUMERIC_LITERAL));
  }

  @Test
  public void hex_literal() {
    assertThat(lexer.lex("0xFF"), hasToken("0xFF", FlexTokenType.NUMERIC_LITERAL));
  }

  @Test
  public void float_literal() {
    assertThat(lexer.lex("12.9E-1"), hasToken("12.9E-1", FlexTokenType.NUMERIC_LITERAL));
    assertThat(lexer.lex(".129e+1"), hasToken(".129e+1", FlexTokenType.NUMERIC_LITERAL));
  }

}
