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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.sonar.api.resources.Qualifiers;

public class ScalaFileTest {

  @Test
  public void shouldHaveFileQualifierForSourceFile() {
    assertThat(new ScalaFile("package", "Class", false).getQualifier(),
        equalTo(Qualifiers.FILE));
  }

  @Test
  public void shouldHaveTestFileQualifierForTestFile() {
    assertThat(new ScalaFile("package", "Class", true).getQualifier(),
        equalTo(Qualifiers.UNIT_TEST_FILE));
  }
}