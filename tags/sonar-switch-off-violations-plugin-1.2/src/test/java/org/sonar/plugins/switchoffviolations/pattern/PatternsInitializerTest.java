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

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.switchoffviolations.Constants;
import org.sonar.plugins.switchoffviolations.SwitchOffViolationsPlugin;
import org.sonar.test.TestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

public class PatternsInitializerTest {

  private Settings settings;
  private PatternsInitializer patternsInitializer;

  @Before
  public void init() {
    settings = new Settings(new PropertyDefinitions(new SwitchOffViolationsPlugin()));
    patternsInitializer = new PatternsInitializer(settings);
  }

  @Test
  public void testNoConfiguration() {
    patternsInitializer.initPatterns();
    Pattern[] patterns = patternsInitializer.getStandardPatterns();
    assertThat(patterns.length).isEqualTo(0);
  }

  @Test
  public void shouldUsePatternsPluginParameter() {
    settings.setProperty(Constants.PATTERNS_PARAMETER_KEY, "org.foo.Bar;*;*\norg.foo.Hello;checkstyle:MagicNumber;[15-200]");
    patternsInitializer.initPatterns();

    Pattern[] patterns = patternsInitializer.getStandardPatterns();
    assertThat(patterns.length).isEqualTo(2);
  }

  @Test
  public void shouldLoadConfigurationFile() throws IOException {
    File file = TestUtils.getResource(getClass(), "filter.txt");
    settings.setProperty(Constants.LOCATION_PARAMETER_KEY, file.getCanonicalPath());
    patternsInitializer.initPatterns();

    Pattern[] patterns = patternsInitializer.getStandardPatterns();
    assertThat(patterns.length).isEqualTo(3);
  }

  @Test
  public void shouldUsePatternsPluginParameterBeforeConfigurationFile() throws IOException {
    // filter.txt defines 2 patterns
    File file = TestUtils.getResource(getClass(), "filter.txt");
    settings.setProperty(Constants.LOCATION_PARAMETER_KEY, file.getCanonicalPath());
    // but there's actually only 1 pattern defined directly via the plugin parameter
    String patternsList = "org.foo.Bar;*;*";
    settings.setProperty(Constants.PATTERNS_PARAMETER_KEY, patternsList);
    patternsInitializer.initPatterns();

    Pattern[] patterns = patternsInitializer.getStandardPatterns();
    assertThat(patterns.length).isEqualTo(1);
  }

  @Test
  public void shouldReturnStandardAndRegexpPatterns() {
    settings.setProperty(Constants.PATTERNS_PARAMETER_KEY, "SONAR-ALL-OFF\norg.foo.Bar;*;*\nSONAR-ALL-ON\norg.foo.Hello;checkstyle:MagicNumber;[15-200]\nSONAR-OFF;SONAR-ON");
    patternsInitializer.initPatterns();

    assertThat(patternsInitializer.getStandardPatterns().length).isEqualTo(2);
    assertThat(patternsInitializer.getDoubleRegexpPatterns().length).isEqualTo(1);
    assertThat(patternsInitializer.getSingleRegexpPatterns().length).isEqualTo(2);
  }

  @Test
  public void shouldReturnExtraPatternForResource() {
    org.sonar.api.resources.File file = new org.sonar.api.resources.File("foo");
    patternsInitializer.addPatternToExcludeResource(file);

    Pattern extraPattern = patternsInitializer.getExtraPattern(file);
    assertThat(extraPattern.matchResource(file)).isTrue();
    assertThat(extraPattern.isCheckLines()).isFalse();
  }

  @Test
  public void shouldReturnExtraPatternForLinesOfResource() {
    org.sonar.api.resources.File file = new org.sonar.api.resources.File("foo");
    Set<LineRange> lineRanges = Sets.newHashSet();
    lineRanges.add(new LineRange(25, 28));
    patternsInitializer.addPatternToExcludeLines(file, lineRanges);

    Pattern extraPattern = patternsInitializer.getExtraPattern(file);
    assertThat(extraPattern.matchResource(file)).isTrue();
    assertThat(extraPattern.getAllLines()).isEqualTo(Sets.newHashSet(25, 26, 27, 28));
  }

  @Test(expected = SonarException.class)
  public void shouldFailIfFileNotFound() {
    settings.setProperty(Constants.LOCATION_PARAMETER_KEY, "/path/to/unknown/file");
    patternsInitializer.initPatterns();
  }

}
