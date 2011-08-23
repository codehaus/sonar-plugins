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

import org.junit.Ignore;
import org.junit.Test;

public class CommentsAnalyzerTest {

  @Test
  public void shouldCountOneSingleLineComment() {
    CommentsAnalyzer commentsAnalyzer = new CommentsAnalyzer("// This is a comment...");
    assertThat(commentsAnalyzer.countCommentLines(), is(1));
  }

  @Test
  public void shouldCountAllSingleLineComments() {
    CommentsAnalyzer commentsAnalyzer = new CommentsAnalyzer("val i = 0\r\n"
        + "//comment1\r\n"
        + "\r\n"
        + "//comment 2// c = 3\r\n"
        + "\r\n"
        + "i = 78"
        + "\r\n"
        + "//comment 3");
    assertThat(commentsAnalyzer.countCommentLines(), is(3));
  }

  @Test
  public void shouldCountOneMultiLineComments() {
    CommentsAnalyzer commentsAnalyzer = new CommentsAnalyzer(" /* This is a multi-line comment\r\n"
        + "* comment1\r\n"
        + "*/");
    assertThat(commentsAnalyzer.countCommentLines(), is(2));
  }

  @Test
  public void shouldCountAllMultiLineComments() {
    CommentsAnalyzer commentsAnalyzer = new CommentsAnalyzer("/* This is a multi-line comment\r\n"
        + "*comment1\r\n"
        + "*/\r\n"
        + "\r\n"
        + "val c = 3\r\n"
        + "val i = 78\r\n"
        + "/* \r\n"
        + "* And another multi-line comment\r\n"
        + "* with two lines\r\n"
        + "*/");
    assertThat(commentsAnalyzer.countCommentLines(), is(4));
  }

  @Ignore
  @Test
  public void shouldCountAllCommentLinesWithoutCommentedOutLinesOfCode() {
    CommentsAnalyzer commentsAnalyzer = new CommentsAnalyzer("//val i = 0\r\n"
        + "//comment1\r\n"
        + "\r\n"
        + "// c = 3\r\n"
        + "\r\n"
        + "i = 78"
        + "\r\n"
        + "//comment 3");
    assertThat(commentsAnalyzer.countCommentLines(), is(2));
  }

  @Test
  public void shouldGiveZeroCommentLinesForEmptySource() {
    CommentsAnalyzer commentsAnalyzer = new CommentsAnalyzer("");
    assertThat(commentsAnalyzer.countCommentLines(), is(0));
  }
}