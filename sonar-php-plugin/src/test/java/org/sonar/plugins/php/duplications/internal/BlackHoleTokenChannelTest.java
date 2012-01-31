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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Test;
import org.sonar.channel.CodeReader;

public class BlackHoleTokenChannelTest {

  @Test
  public void shouldConsume() {
    BlackHoleTokenChannel channel = new BlackHoleTokenChannel("ABC");
    TokenQueue output = mock(TokenQueue.class);
    CodeReader codeReader = new CodeReader("ABCD");

    assertThat(channel.consume(codeReader, output), is(true));
    assertThat(codeReader.getLinePosition(), is(1));
    assertThat(codeReader.getColumnPosition(), is(3));
    verifyZeroInteractions(output);
  }

}
