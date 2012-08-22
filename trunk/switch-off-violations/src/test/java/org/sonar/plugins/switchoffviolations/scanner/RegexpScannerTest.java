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

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonar.api.resources.JavaFile;
import org.sonar.plugins.switchoffviolations.pattern.LineRange;
import org.sonar.plugins.switchoffviolations.pattern.Pattern;
import org.sonar.plugins.switchoffviolations.pattern.PatternsInitializer;
import org.sonar.test.TestUtils;

import java.nio.charset.Charset;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class RegexpScannerTest {

  private static final Charset UTF_8 = Charset.forName("UTF-8");

  private RegexpScanner regexpScanner;

  private JavaFile javaFile;
  @Mock
  private PatternsInitializer patternsInitializer;
  @Mock
  private Pattern singleRegexpPattern;
  @Mock
  private Pattern doubleRegexpPattern1;
  @Mock
  private Pattern doubleRegexpPattern2;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);

    when(singleRegexpPattern.getRegexp1()).thenReturn("@SONAR-IGNORE-ALL");
    when(doubleRegexpPattern1.getRegexp1()).thenReturn("// SONAR-OFF");
    when(doubleRegexpPattern1.getRegexp2()).thenReturn("// SONAR-ON");
    when(doubleRegexpPattern2.getRegexp1()).thenReturn("// FOO-OFF");
    when(doubleRegexpPattern2.getRegexp2()).thenReturn("// FOO-ON");
    when(patternsInitializer.getSingleRegexpPatterns()).thenReturn(new Pattern[] {singleRegexpPattern});
    when(patternsInitializer.getDoubleRegexpPatterns()).thenReturn(new Pattern[] {doubleRegexpPattern1, doubleRegexpPattern2});

    regexpScanner = new RegexpScanner(patternsInitializer);
    verify(patternsInitializer, times(1)).getSingleRegexpPatterns();
    verify(patternsInitializer, times(1)).getDoubleRegexpPatterns();

    javaFile = new JavaFile("org.sonar.test.MyFile");
  }

  @Test
  public void shouldDoNothing() throws Exception {
    regexpScanner.scan(javaFile, TestUtils.getResource(getClass(), "file-with-no-regexp.txt"), UTF_8);

    verifyNoMoreInteractions(patternsInitializer);
  }

  @Test
  public void shouldAddPatternToExcludeFile() throws Exception {
    regexpScanner.scan(javaFile, TestUtils.getResource(getClass(), "file-with-single-regexp.txt"), UTF_8);

    verify(patternsInitializer, times(1)).addPatternToExcludeResource(javaFile);
    verifyNoMoreInteractions(patternsInitializer);
  }

  @Test
  public void shouldAddPatternToExcludeFileEvenIfAlsoDoubleRegexps() throws Exception {
    regexpScanner.scan(javaFile, TestUtils.getResource(getClass(), "file-with-single-regexp-and-double-regexp.txt"), UTF_8);

    verify(patternsInitializer, times(1)).addPatternToExcludeResource(javaFile);
    verifyNoMoreInteractions(patternsInitializer);
  }

  @Test
  public void shouldAddPatternToExcludeLines() throws Exception {
    regexpScanner.scan(javaFile, TestUtils.getResource(getClass(), "file-with-double-regexp.txt"), UTF_8);

    Set<LineRange> lineRanges = Sets.newHashSet();
    lineRanges.add(new LineRange(21, 25));
    verify(patternsInitializer, times(1)).addPatternToExcludeLines(javaFile, lineRanges);
    verifyNoMoreInteractions(patternsInitializer);
  }

  @Test
  public void shouldAddPatternToExcludeLinesTillTheEnd() throws Exception {
    regexpScanner.scan(javaFile, TestUtils.getResource(getClass(), "file-with-double-regexp-unfinished.txt"), UTF_8);

    Set<LineRange> lineRanges = Sets.newHashSet();
    lineRanges.add(new LineRange(21, 34));
    verify(patternsInitializer, times(1)).addPatternToExcludeLines(javaFile, lineRanges);
    verifyNoMoreInteractions(patternsInitializer);
  }

  @Test
  public void shouldAddPatternToExcludeSeveralLineRanges() throws Exception {
    regexpScanner.scan(javaFile, TestUtils.getResource(getClass(), "file-with-double-regexp-twice.txt"), UTF_8);

    Set<LineRange> lineRanges = Sets.newHashSet();
    lineRanges.add(new LineRange(21, 25));
    lineRanges.add(new LineRange(29, 33));
    verify(patternsInitializer, times(1)).addPatternToExcludeLines(javaFile, lineRanges);
    verifyNoMoreInteractions(patternsInitializer);
  }

  @Test
  public void shouldAddPatternToExcludeLinesWithWrongOrder() throws Exception {
    regexpScanner.scan(javaFile, TestUtils.getResource(getClass(), "file-with-double-regexp-wrong-order.txt"), UTF_8);

    Set<LineRange> lineRanges = Sets.newHashSet();
    lineRanges.add(new LineRange(25, 35));
    verify(patternsInitializer, times(1)).addPatternToExcludeLines(javaFile, lineRanges);
    verifyNoMoreInteractions(patternsInitializer);
  }

  @Test
  public void shouldAddPatternToExcludeLinesWithMess() throws Exception {
    regexpScanner.scan(javaFile, TestUtils.getResource(getClass(), "file-with-double-regexp-mess.txt"), UTF_8);

    Set<LineRange> lineRanges = Sets.newHashSet();
    lineRanges.add(new LineRange(21, 29));
    verify(patternsInitializer, times(1)).addPatternToExcludeLines(javaFile, lineRanges);
    verifyNoMoreInteractions(patternsInitializer);
  }

}
