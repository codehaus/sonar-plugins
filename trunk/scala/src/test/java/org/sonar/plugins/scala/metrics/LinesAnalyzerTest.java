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
package org.sonar.plugins.scala.metrics;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.sonar.plugins.scala.util.StringUtils;

public class LinesAnalyzerTest {

  @Test
  public void shouldCountOneLine() throws IOException {
    LinesAnalyzer linesAnalyzer = getLinesAnalyzer("val i = 0");
    assertThat(linesAnalyzer.countLines(), is(1));
  }

  @Test
  public void shouldCountAllLines() throws IOException {
    LinesAnalyzer linesAnalyzer = getLinesAnalyzer("val i = 0\r\n"
        + "println(\"Hallo\")\r\n"
        + "\r\n"
        + "i = 2");
    assertThat(linesAnalyzer.countLines(), is(4));
  }

  @Test
  public void shouldGiveZeroLinesForEmptySource() throws IOException {
    LinesAnalyzer linesAnalyzer = getLinesAnalyzer("");
    assertThat(linesAnalyzer.countLines(), is(0));
  }

  private LinesAnalyzer getLinesAnalyzer(String source) throws IOException {
    List<String> lines = StringUtils.convertStringToListOfLines(source);
    CommentsAnalyzer commentsAnalyzer = new CommentsAnalyzer(source);
    return new LinesAnalyzer(lines, commentsAnalyzer);
  }
}