/*
 * Sonar PHP Plugin
 * Copyright (C) 2010 Codehaus Sonar Plugins
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
package org.sonar.plugins.php.duplications.internal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TokenQueueTest {

  TokenQueue tokenQueue;

  @Before
  public void initTest() {
    List<Token> tokenList = new ArrayList<Token>();
    tokenList.add(new Token("a", 1, 0));
    tokenList.add(new Token("bc", 1, 2));
    tokenList.add(new Token("def", 1, 5));
    tokenQueue = new TokenQueue(tokenList);
  }

  @Test
  public void shouldPeekToken() {
    Token token = tokenQueue.peek();
    assertThat(token, is(new Token("a", 1, 0)));
    assertThat(tokenQueue.size(), is(3));
  }

  @Test
  public void shouldPollToken() {
    Token token = tokenQueue.poll();
    assertThat(token, is(new Token("a", 1, 0)));
    assertThat(tokenQueue.size(), is(2));
  }

  @Test
  public void shouldPushTokenAtBegining() {
    Token pushedToken = new Token("push", 1, 0);
    List<Token> pushedTokenList = new ArrayList<Token>();
    pushedTokenList.add(pushedToken);
    tokenQueue.pushForward(pushedTokenList);
    assertThat(tokenQueue.peek(), is(pushedToken));
    assertThat(tokenQueue.size(), is(4));
  }

}
