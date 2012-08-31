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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.SonarException;
import org.sonar.test.TestUtils;

import java.io.File;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class PatternDecoderTest {

  private PatternDecoder decoder = new PatternDecoder();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void shouldReadFileWithAllTypesOfPatterns() {
    File file = TestUtils.getResource(getClass(), "valid.txt");
    List<Pattern> patterns = decoder.decode(file);
    assertThat(patterns.size()).isEqualTo(7);
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
    assertThat(patterns.size()).isEqualTo(5);
  }

  @Test
  public void shouldCheckFormatOfResource() {
    assertThat(decoder.isResource("")).isEqualTo(false);
    assertThat(decoder.isResource("*")).isEqualTo(true);
    assertThat(decoder.isResource("com.foo.*")).isEqualTo(true);
  }

  @Test
  public void shouldCheckFormatOfRule() {
    assertThat(decoder.isRule("")).isEqualTo(false);
    assertThat(decoder.isRule("*")).isEqualTo(true);
    assertThat(decoder.isRule("com.foo.*")).isEqualTo(true);
  }

  @Test
  public void shouldCheckFormatOfLinesRange() {
    assertThat(decoder.isLinesRange("")).isEqualTo(false);
    assertThat(decoder.isLinesRange("   ")).isEqualTo(false);
    assertThat(decoder.isLinesRange("12")).isEqualTo(false);
    assertThat(decoder.isLinesRange("12,212")).isEqualTo(false);

    assertThat(decoder.isLinesRange("*")).isEqualTo(true);
    assertThat(decoder.isLinesRange("[]")).isEqualTo(true);
    assertThat(decoder.isLinesRange("[13]")).isEqualTo(true);
    assertThat(decoder.isLinesRange("[13,24]")).isEqualTo(true);
    assertThat(decoder.isLinesRange("[13,24,25-500]")).isEqualTo(true);
    assertThat(decoder.isLinesRange("[24-65]")).isEqualTo(true);
    assertThat(decoder.isLinesRange("[13,24-65,84-89,122]")).isEqualTo(true);
  }

  @Test
  public void shouldReadStarPatterns() {
    Pattern pattern = decoder.decodeLine("*;*;*");
    assertThat(pattern.getResourcePattern().toString()).isEqualTo("*");
    assertThat(pattern.getRulePattern().toString()).isEqualTo("*");
    assertThat(pattern.isCheckLines()).isEqualTo(false);
  }

  @Test
  public void shouldReadLineIds() {
    Pattern pattern = decoder.decodeLine("*;*;[10,25,98]");
    assertThat(pattern.isCheckLines()).isEqualTo(true);
    assertThat(pattern.getAllLines().size()).isEqualTo(3);
    assertThat(pattern.getAllLines()).contains(10, 25, 98);
  }

  @Test
  public void shouldReadRangeOfLineIds() {
    Pattern pattern = decoder.decodeLine("*;*;[10-12,25,97-100]");
    assertThat(pattern.isCheckLines()).isEqualTo(true);
    assertThat(pattern.getAllLines().size()).isEqualTo(8);
    assertThat(pattern.getAllLines()).contains(10, 11, 12, 25, 97, 98, 99, 100);
  }

  @Test
  public void shouldNotExcludeLines() {
    // [] is different than *
    // - all violations are excluded on *
    // * no violations are excluded on []
    Pattern pattern = decoder.decodeLine("*;*;[]");
    assertThat(pattern.isCheckLines()).isEqualTo(true);
    assertThat(pattern.getAllLines().size()).isEqualTo(0);
  }

  @Test
  public void shouldReadDoubleRegexpPattern() {
    Pattern pattern = decoder.decodeLine("SONAR-OFF;SONAR-ON");
    assertThat(pattern.getResourcePattern()).isNull();
    assertThat(pattern.getRegexp1()).isEqualTo("SONAR-OFF");
    assertThat(pattern.getRegexp2()).isEqualTo("SONAR-ON");
  }

  @Test
  public void shouldReadSingleRegexpPattern() {
    Pattern pattern = decoder.decodeLine("SONAR-ALL-OFF");
    assertThat(pattern.getResourcePattern()).isNull();
    assertThat(pattern.getRegexp1()).isEqualTo("SONAR-ALL-OFF");
  }

  @Test
  public void shouldFailIfUnexistingFile() {
    thrown.expect(SonarException.class);
    thrown.expectMessage("Fail to load the file");

    decoder.decode(new File("foo"));
  }

  @Test
  public void shouldFailToReadUncorrectLine1() {
    thrown.expect(SonarException.class);
    thrown.expectMessage("Unvalid format. The following line has more than 3 fields separated by comma");

    decoder.decode(";;;;");
  }

  @Test
  public void shouldFailToReadUncorrectLine3() {
    thrown.expect(SonarException.class);
    thrown.expectMessage("Unvalid format. The first field does not define a resource pattern");

    decoder.decode(";*;*");
  }

  @Test
  public void shouldFailToReadUncorrectLine4() {
    thrown.expect(SonarException.class);
    thrown.expectMessage("Unvalid format. The second field does not define a rule pattern");

    decoder.decode("*;;*");
  }

  @Test
  public void shouldFailToReadUncorrectLine5() {
    thrown.expect(SonarException.class);
    thrown.expectMessage("Unvalid format. The third field does not define a range of lines");

    decoder.decode("*;*;blabla");
  }

  @Test
  public void shouldFailToReadUncorrectLine6() {
    thrown.expect(SonarException.class);
    thrown.expectMessage("Unvalid format. The first field does not define a regular expression");

    decoder.decode(";ON");
  }

  @Test
  public void shouldFailToReadUncorrectLine7() {
    thrown.expect(SonarException.class);
    thrown.expectMessage("Unvalid format. The second field does not define a regular expression");

    decoder.decode("OFF;");
  }
}
