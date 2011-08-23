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

import java.util.List;

import org.sonar.plugins.scala.compiler.Lexer;

/**
 * This class implements the computation of basic
 * line metrics for a {@link Comment}.
 *
 * @author Felix Müller
 * @since 0.1
 */
public class CommentsAnalyzer {

  private final List<Comment> comments;

  public CommentsAnalyzer(String source) {
    comments = new Lexer().getComments(source);
  }

  public int countCommentLines() {
    int commentLines = 0;
    for (Comment comment : comments) {
      if (!comment.isHeaderComment()) {
        commentLines += comment.getNumberOfLines();
      }
    }
    return commentLines;
  }

  public int countCommentBlankLines() {
    int commentBlankLines = 0;
    for (Comment comment : comments) {
      if (!comment.isHeaderComment()) {
        commentBlankLines += comment.getNumberOfBlankLines();
      }
    }
    return commentBlankLines;
  }

  public int countHeaderCommentLines() {
    int headerCommentLines = 0;
    for (Comment comment : comments) {
      if (comment.isHeaderComment()) {
        headerCommentLines += comment.getNumberOfLines();
      }
    }
    return headerCommentLines;
  }

  public int countCommentedOutLinesOfCode() {
    int commentedOutLinesOfCode = 0;
    for (Comment comment : comments) {
      if (!comment.isHeaderComment()) {
        commentedOutLinesOfCode += comment.getNumberOfCommentedOutLinesOfCode();
      }
    }
    return commentedOutLinesOfCode;
  }
}