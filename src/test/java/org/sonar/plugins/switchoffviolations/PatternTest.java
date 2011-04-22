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

import org.junit.Test;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.rules.Rule;
import org.sonar.plugins.switchoffviolations.Pattern;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

public class PatternTest {
  
  @Test(expected = IllegalArgumentException.class)
  public void lineRangeShouldBeOrdered() {
    new Pattern.LineRange(25, 12);
  }

  @Test
  public void shouldConvertLineRangeToLines() {
    Pattern.LineRange range = new Pattern.LineRange(12, 15);
    assertThat(range.toLines().size(), is(4));
    assertThat(range.toLines(), hasItems(12, 13, 14, 15));
  }

  @Test
  public void shouldTestInclusionInRangeOfLines() {
    Pattern.LineRange range = new Pattern.LineRange(12, 15);
    assertThat(range.in(3), is(false));
    assertThat(range.in(12), is(true));
    assertThat(range.in(13), is(true));
    assertThat(range.in(14), is(true));
    assertThat(range.in(15), is(true));
    assertThat(range.in(16), is(false));    
  }

  @Test
  public void shouldMatchLines() {
    Pattern pattern = new Pattern("*", "*");
    pattern.addLine(12).addLine(15).addLineRange(20, 25);

    assertThat(pattern.matchLine(3), is(false));
    assertThat(pattern.matchLine(12), is(true));
    assertThat(pattern.matchLine(14), is(false));
    assertThat(pattern.matchLine(21), is(true));
    assertThat(pattern.matchLine(6599), is(false));
  }

  @Test
  public void shouldMatchJavaFile() {
    JavaFile javaFile = new JavaFile("org.foo.Bar");
    assertThat(new Pattern("org.foo.Bar", "*").matchResource(javaFile), is(true));
    assertThat(new Pattern("org.foo.*", "*").matchResource(javaFile), is(true));
    assertThat(new Pattern("*Bar", "*").matchResource(javaFile), is(true));
    assertThat(new Pattern("*", "*").matchResource(javaFile), is(true));
    assertThat(new Pattern("org.*.?ar", "*").matchResource(javaFile), is(true));

    assertThat(new Pattern("org.other.Hello", "*").matchResource(javaFile), is(false));
    assertThat(new Pattern("org.foo.Hello", "*").matchResource(javaFile), is(false));
    assertThat(new Pattern("org.*.??ar", "*").matchResource(javaFile), is(false));
  }

  @Test
  public void shouldMatchRule() {
    Rule rule = Rule.create("checkstyle", "IllegalRegexp", "");
    assertThat(new Pattern("*", "*").matchRule(rule), is(true));
    assertThat(new Pattern("*", "checkstyle:*").matchRule(rule), is(true));
    assertThat(new Pattern("*", "checkstyle:IllegalRegexp").matchRule(rule), is(true));
    assertThat(new Pattern("*", "checkstyle:Illegal*").matchRule(rule), is(true));
    assertThat(new Pattern("*", "*:*Illegal*").matchRule(rule), is(true));

    assertThat(new Pattern("*", "pmd:IllegalRegexp").matchRule(rule), is(false));
    assertThat(new Pattern("*", "pmd:*").matchRule(rule), is(false));
    assertThat(new Pattern("*", "*:Foo*IllegalRegexp").matchRule(rule), is(false));  

  }
}
