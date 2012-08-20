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

package org.sonar.plugins.switchoffviolations.pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItems;

import org.sonar.plugins.switchoffviolations.pattern.Pattern;
import org.sonar.plugins.switchoffviolations.pattern.PatternDecoder;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.sonar.api.utils.SonarException;
import org.sonar.test.TestUtils;

public class PatternDecoderTest {

  private PatternDecoder decoder = new PatternDecoder();

  @Test(expected = SonarException.class)
  public void shouldFailIfNotValid() {
    File file = TestUtils.getResource(getClass(), "unvalid.txt");
    decoder.decode(file);
  }

  @Test(expected = SonarException.class)
  public void shouldFailIfUnexistingFile() {
    decoder.decode(new File("foo"));
  }

  @Test
  public void shouldReadFile() {
    File file = TestUtils.getResource(getClass(), "valid.txt");
    List<Pattern> patterns = decoder.decode(file);
    assertThat(patterns.size(), is(5));
  }

  @Test
  public void shouldReadString() {
    String patternsList = "# a comment followed by a blank line\n\n" +
      "# suppress all violations\n" +
      "*;*;*\n\n" +
      "# exclude a Java file\n" +
      "com.foo.Bar;*;*\n\n" +
      "# exclude a Java package\n" +
      "com.foo.*;*;*\n\n" +
      "# exclude a specific rule\n" +
      "*;checkstyle:IllegalRegexp;*\n\n" +
      "# exclude a specific rule on a specific file\n" +
      "com.foo.Bar;checkstyle:IllegalRegexp;*\n";
    List<Pattern> patterns = decoder.decode(patternsList);
    assertThat(patterns.size(), is(5));
  }

  @Test
  public void shouldCheckFormatOfResource() {
    assertThat(decoder.isResource(""), is(false));
    assertThat(decoder.isResource("*"), is(true));
    assertThat(decoder.isResource("com.foo.*"), is(true));
  }

  @Test
  public void shouldCheckFormatOfRule() {
    assertThat(decoder.isRule(""), is(false));
    assertThat(decoder.isRule("*"), is(true));
    assertThat(decoder.isRule("com.foo.*"), is(true));
  }

  @Test
  public void shouldCheckFormatOfLinesRange() {
    assertThat(decoder.isLinesRange(""), is(false));
    assertThat(decoder.isLinesRange("   "), is(false));
    assertThat(decoder.isLinesRange("12"), is(false));
    assertThat(decoder.isLinesRange("12,212"), is(false));

    assertThat(decoder.isLinesRange("*"), is(true));
    assertThat(decoder.isLinesRange("[]"), is(true));
    assertThat(decoder.isLinesRange("[13]"), is(true));
    assertThat(decoder.isLinesRange("[13,24]"), is(true));
    assertThat(decoder.isLinesRange("[13,24,25-500]"), is(true));
    assertThat(decoder.isLinesRange("[24-65]"), is(true));
    assertThat(decoder.isLinesRange("[13,24-65,84-89,122]"), is(true));
  }

  @Test
  public void shouldReadStarPatterns() {
    Pattern pattern = decoder.decodeLine("*;*;*");
    assertThat(pattern.getResourcePattern().toString(), is("*"));
    assertThat(pattern.getRulePattern().toString(), is("*"));
    assertThat(pattern.isCheckLines(), is(false));
  }

  @Test
  public void shouldReadLineIds() {
    Pattern pattern = decoder.decodeLine("*;*;[10,25,98]");
    assertThat(pattern.isCheckLines(), is(true));
    assertThat(pattern.getAllLines().size(), is(3));
    assertThat(pattern.getAllLines(), hasItems(10, 25, 98));
  }

  @Test
  public void shouldReadRangeOfLineIds() {
    Pattern pattern = decoder.decodeLine("*;*;[10-12,25,97-100]");
    assertThat(pattern.isCheckLines(), is(true));
    assertThat(pattern.getAllLines().size(), is(8));
    assertThat(pattern.getAllLines(), hasItems(10, 11, 12, 25, 97, 98, 99, 100));
  }

  @Test
  public void shouldNotExcludeLines() {
    // [] is different than *
    // - all violations are excluded on *
    // * no violations are excluded on []
    Pattern pattern = decoder.decodeLine("*;*;[]");
    assertThat(pattern.isCheckLines(), is(true));
    assertThat(pattern.getAllLines().size(), is(0));
  }
}
