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

import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;
import org.sonar.plugins.scala.language.Comment;

public class CommentsAnalyzerTest {

  @Ignore
  @Test
  public void shouldCountAllCommentLines() {
    // TODO add implementation
  }

  @Ignore
  @Test
  public void shouldCountAllHeaderCommentLines() {
    // TODO add implementation
  }

  @Ignore
  @Test
  public void shouldCountAllCommentedOutLinesOfCode() {
    // TODO add implementation
  }

  @Test
  public void shouldCountZeroCommentLinesForEmptyCommentsList() {
    CommentsAnalyzer commentAnalyzer = new CommentsAnalyzer(Collections.<Comment>emptyList());
    assertThat(commentAnalyzer.countCommentLines(), is(0));
  }

  @Test
  public void shouldCountZeroHeaderCommentLinesForEmptyCommentsList() {
    CommentsAnalyzer commentAnalyzer = new CommentsAnalyzer(Collections.<Comment>emptyList());
    assertThat(commentAnalyzer.countHeaderCommentLines(), is(0));
  }

  @Test
  public void shouldCountZeroCommentedOutLinesOfCodeForEmptyCommentsList() {
    CommentsAnalyzer commentAnalyzer = new CommentsAnalyzer(Collections.<Comment>emptyList());
    assertThat(commentAnalyzer.countCommentedOutLinesOfCode(), is(0));
  }
}