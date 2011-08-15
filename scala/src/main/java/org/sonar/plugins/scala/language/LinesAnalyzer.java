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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the computation of the several
 * base metrics for a {@link ScalaFile}.
 *
 * @author Felix Müller
 * @since 0.1
 */
public class LinesAnalyzer {

  private static Logger LOGGER = LoggerFactory.getLogger(LinesAnalyzer.class);

  private final String source;
  private final List<String> lines = new ArrayList<String>();

  public LinesAnalyzer(String source) {
    this.source = source;
    convertSourceToListOfLines();
  }

  private void convertSourceToListOfLines() {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new StringReader(source));
      String line = null;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    } catch (IOException ioe) {
      LOGGER.warn("Error while reading the lines of a given source", ioe);
    } finally {
      IOUtils.closeQuietly(reader);
    }
  }

  public int countLines() {
    return lines.size();
  }

  public int countLinesOfCode() {
    return countLines() - countBlankLines() - countCommentLines() - countCommentBlankLines()
        - countHeaderCommentLines() - countCommentedOutLinesOfCode();
  }

  private int countBlankLines() {
    int numberOfBlankLines = 0;
    for (String line : lines) {
      if (isBlankLine(line)) {
        numberOfBlankLines++;
      }
    }
    return numberOfBlankLines;
  }

  private boolean isBlankLine(String line) {
    for (int i = 0; i < line.length(); i++) {
      if (!Character.isWhitespace(line.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  private int countHeaderCommentLines() {
    return 0;
  }

  public int countCommentLines() {
    return 0;
  }

  public int countCommentBlankLines() {
    return 0;
  }

  public int countCommentedOutLinesOfCode() {
    return 0;
  }
}