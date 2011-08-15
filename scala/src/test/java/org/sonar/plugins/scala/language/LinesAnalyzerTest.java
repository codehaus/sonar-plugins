/*
 * Sonar Scala Plugin
 * Copyright (C) 2011 Felix Müller
 * felix.mueller.berlin@googlemail.com
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
package org.sonar.plugins.scala.language;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class LinesAnalyzerTest {

  @Test
  public void shouldCountOneLine() {
    LinesAnalyzer fileMetrics = new LinesAnalyzer("val i = 0");
    assertThat(fileMetrics.countLines(), is(1));
  }

  @Test
  public void shouldCountAllNumberOfLines() {
    LinesAnalyzer fileMetrics = new LinesAnalyzer("val i = 0\r\n"
        + "println(\"Hallo\")\r\n"
        + "\r\n"
        + "i = 2");
    assertThat(fileMetrics.countLines(), is(4));
  }

  @Test
  public void shouldGiveZeroNumberOfLinesForEmptySource() {
    LinesAnalyzer fileMetrics = new LinesAnalyzer("");
    assertThat(fileMetrics.countLines(), is(0));
  }
}