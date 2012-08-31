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

package org.sonar.plugins.switchoffviolations;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.switchoffviolations.pattern.Pattern;
import org.sonar.plugins.switchoffviolations.pattern.PatternDecoder;
import org.sonar.plugins.switchoffviolations.pattern.PatternsInitializer;

import java.io.IOException;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SwitchOffViolationsFilterTest {

  public static final Rule CHECKSTYLE_RULE = Rule.create("checkstyle", "MagicNumber", "");
  public static final JavaFile JAVA_FILE = new JavaFile("org.foo.Hello");

  private PatternsInitializer patternsInitializer;
  private SwitchOffViolationsFilter filter;

  @Before
  public void init() {
    patternsInitializer = mock(PatternsInitializer.class);
    when(patternsInitializer.getStandardPatterns()).thenReturn(new Pattern[0]);

    filter = new SwitchOffViolationsFilter(patternsInitializer);
  }

  @Test
  public void shouldBeDeactivatedWhenPropertyIsMissing() {
    assertThat(filter.isIgnored(Violation.create(CHECKSTYLE_RULE, JAVA_FILE))).isFalse();
  }

  @Test
  public void shouldBeIgnoredWithStandardPatterns() throws IOException {
    when(patternsInitializer.getStandardPatterns()).thenReturn(createPatterns("org.foo.Bar;*;*\norg.foo.Hello;checkstyle:MagicNumber;[15-200]"));

    assertThat(filter.isIgnored(Violation.create(CHECKSTYLE_RULE, JAVA_FILE).setLineId(150))).isTrue();
  }

  @Test
  public void shouldNotBeIgnoredWithStandardPatterns() throws IOException {
    when(patternsInitializer.getStandardPatterns()).thenReturn(createPatterns("org.foo.Bar;*;*\norg.foo.Hello;checkstyle:MagicNumber;[15-200]"));

    assertThat(filter.isIgnored(Violation.create(CHECKSTYLE_RULE, JAVA_FILE).setLineId(5))).isFalse();
  }

  @Test
  public void shouldBeIgnoredWithExtraPattern() throws IOException {
    when(patternsInitializer.getExtraPattern(JAVA_FILE)).thenReturn(createPatterns("org.foo.Hello;*;[15-200]")[0]);

    assertThat(filter.isIgnored(Violation.create(CHECKSTYLE_RULE, JAVA_FILE).setLineId(150))).isTrue();
  }

  @Test
  public void shouldNotBeIgnoredWithExtraPattern() throws IOException {
    when(patternsInitializer.getExtraPattern(JAVA_FILE)).thenReturn(createPatterns("org.foo.Hello;*;[15-200]")[0]);

    assertThat(filter.isIgnored(Violation.create(CHECKSTYLE_RULE, JAVA_FILE).setLineId(5))).isFalse();
  }

  private Pattern[] createPatterns(String line) {
    List<Pattern> patterns = new PatternDecoder().decode(line);
    return patterns.toArray(new Pattern[patterns.size()]);
  }
}
