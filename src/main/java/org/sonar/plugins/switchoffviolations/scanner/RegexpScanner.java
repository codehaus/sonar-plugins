/*
 * Sonar Switch Off Violations Plugin
 * Copyright (C) 2011 SonarSource
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

package org.sonar.plugins.switchoffviolations.scanner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.switchoffviolations.pattern.LineRange;
import org.sonar.plugins.switchoffviolations.pattern.Pattern;
import org.sonar.plugins.switchoffviolations.pattern.PatternsInitializer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

public class RegexpScanner implements BatchExtension {

  private static final Logger LOG = LoggerFactory.getLogger(RegexpScanner.class);

  private PatternsInitializer patternsInitializer;
  private List<java.util.regex.Pattern> singleRegexpPatterns;
  private List<DoubleRegexpMatcher> doubleRegexpMatchers;

  // fields to be reset at every new scan
  private DoubleRegexpMatcher currentMatcher;
  private int fileLength;
  private List<LineExclusion> lineExclusions;
  private LineExclusion currentLineExclusion;

  public RegexpScanner(PatternsInitializer patternsInitializer) {
    this.patternsInitializer = patternsInitializer;

    lineExclusions = Lists.newArrayList();
    singleRegexpPatterns = Lists.newArrayList();
    doubleRegexpMatchers = Lists.newArrayList();

    for (Pattern pattern : this.patternsInitializer.getSingleRegexpPatterns()) {
      singleRegexpPatterns.add(java.util.regex.Pattern.compile(pattern.getRegexp1()));
    }
    for (Pattern pattern : this.patternsInitializer.getDoubleRegexpPatterns()) {
      doubleRegexpMatchers.add(new DoubleRegexpMatcher(
          java.util.regex.Pattern.compile(pattern.getRegexp1()),
          java.util.regex.Pattern.compile(pattern.getRegexp2())));
    }

    init();
  }

  private void init() {
    currentMatcher = null;
    fileLength = 0;
    lineExclusions.clear();
    currentLineExclusion = null;
  }

  public void scan(Resource<?> resource, File file, Charset sourcesEncoding) throws IOException {
    LOG.debug("Scanning {}", resource.getKey());
    init();

    List<String> lines = FileUtils.readLines(file, sourcesEncoding.name());
    int lineIndex = 0;
    for (String line : lines) {
      lineIndex++;
      if (line.trim().length() == 0) {
        continue;
      }

      // first check the single regexp patterns that can be used to totally exclude a file
      for (java.util.regex.Pattern pattern : singleRegexpPatterns) {
        if (pattern.matcher(line).find()) {
          patternsInitializer.addPatternToExcludeResource(resource);
          // nothing more to do on this file
          LOG.debug("- Exclusion pattern '{}': every violation in this file will be ignored.", pattern);
          return;
        }
      }

      // then check the double regexps if we're still here
      checkDoubleRegexps(line, lineIndex);
    }

    // now create the new line-based pattern for this file if there are exclusions
    fileLength = lineIndex;
    if (!lineExclusions.isEmpty()) {
      Set<LineRange> lineRanges = convertLineExclusionsToLineRanges();
      LOG.debug("- Line exclusions found: {}", lineRanges);
      patternsInitializer.addPatternToExcludeLines(resource, lineRanges);
    }
  }

  private Set<LineRange> convertLineExclusionsToLineRanges() {
    Set<LineRange> lineRanges = Sets.newHashSet();
    for (LineExclusion lineExclusion : lineExclusions) {
      lineRanges.add(lineExclusion.toLineRange());
    }
    return lineRanges;
  }

  private void checkDoubleRegexps(String line, int lineIndex) {
    if (currentMatcher == null) {
      for (DoubleRegexpMatcher matcher : doubleRegexpMatchers) {
        if (matcher.matchesFirstPattern(line)) {
          startExclusion(lineIndex);
          currentMatcher = matcher;
          break;
        }
      }
    } else {
      if (currentMatcher.matchesSecondPattern(line)) {
        endExclusion(lineIndex);
        currentMatcher = null;
      }
    }
  }

  private void startExclusion(int lineIndex) {
    currentLineExclusion = new LineExclusion(lineIndex);
    lineExclusions.add(currentLineExclusion);
  }

  private void endExclusion(int lineIndex) {
    currentLineExclusion.setEnd(lineIndex);
    currentLineExclusion = null;
  }

  private class LineExclusion {

    private int start;
    private int end;

    LineExclusion(int start) {
      this.start = start;
      this.end = -1;
    }

    void setEnd(int end) {
      this.end = end;
    }

    public LineRange toLineRange() {
      return new LineRange(start, (end == -1 ? fileLength : end));
    }

  }

  private static class DoubleRegexpMatcher {

    private java.util.regex.Pattern firstPattern;
    private java.util.regex.Pattern secondPattern;

    DoubleRegexpMatcher(java.util.regex.Pattern firstPattern, java.util.regex.Pattern secondPattern) {
      this.firstPattern = firstPattern;
      this.secondPattern = secondPattern;
    }

    boolean matchesFirstPattern(String line) {
      return firstPattern.matcher(line).find();
    }

    boolean matchesSecondPattern(String line) {
      return secondPattern.matcher(line).find();
    }

  }

}
