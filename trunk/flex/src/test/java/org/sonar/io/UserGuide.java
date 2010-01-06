/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.io;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import org.sonar.io.channel.AbstractTokenChannel;
import org.sonar.io.channel.BlackHoleChannel;

import java.util.List;
import java.util.ArrayList;
import java.io.StringReader;
import java.io.StringWriter;

import com.google.common.collect.Lists;

public class UserGuide {
  @Test
  public void runDispatcher() {
    List<String> actual = new ArrayList<String>();
    List<Channel> channels = new ArrayList<Channel>();
    channels.add(new ATokenChannel(actual));
    ChannelDispatcher dispatcher = new ChannelDispatcher(channels);
    CodeReader code = new CodeReader(new StringReader("a string-to-be read-"));
    dispatcher.read(code);
    List<String> expected = Lists.newArrayList("a string", "to", "be read");

    assertThat(expected, is(actual));
  }


  @Test
  public void testCobolGrammar() {
    List<String> actual = new ArrayList<String>();
    List<Channel> channels = new ArrayList<Channel>();
    channels.add(new BlackHoleChannel(' ', '\t', '\f', ';', '\r', '\n'));
    ChannelDispatcher dispatcher = new ChannelDispatcher(channels);
    CodeReader code = new CodeReader(new StringReader("a string-to-be read-"));
    dispatcher.read(code);
    List<String> expected = Lists.newArrayList("a string", "to", "be read");

    assertThat(expected, is(actual));

  }

   public class ATokenChannel extends AbstractTokenChannel{
     public ATokenChannel(List<String> list) {
       super(list);
     }

     public boolean read(CodeReader code) {
       int nextChar = code.peek();
       StringWriter sw = new StringWriter();
       if (nextChar != '-') {
         while (nextChar != '-' && nextChar != -1) {
           code.pop(sw);
           nextChar = code.peek();
         }
         addToken(sw);
         return true;
       }
       return false;
     }
   }
}
