/*
 * Sonar C-Rules Plugin
 * Copyright (C) 2010 SonarSource
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

package org.sonar.c.checks;

import static org.sonar.c.checks.CheckMatchers.*;
import static org.sonar.c.checks.CheckUtils.*;

import java.text.MessageFormat;

import org.junit.Test;

public class HiddenIdentifiersCheckTest {

  private static final String getFormattedMessage(String identifier, int line) {
    return MessageFormat.format("The identifier \"{0}\" was first declared at line {1}.", identifier, line);
  }

  @Test
  public void testCheck() {
    setCurrentSourceFile(scanFile("/checks/hiddenIdentifiers.c", new HiddenIdentifiersCheck()));

    assertNumberOfViolations(15);

    assertViolation().atLine(13).withMessage(getFormattedMessage("fileA", 11));
    assertViolation().atLine(29).withMessage(getFormattedMessage("errno", 10));
    assertViolation().atLine(32).withMessage(getFormattedMessage("fileA", 11));
    assertViolation().atLine(43).withMessage(getFormattedMessage("fileA", 11));
    assertViolation().atLine(46).withMessage(getFormattedMessage("a", 26));
    assertViolation().atLine(54).withMessage(getFormattedMessage("a", 26));
    assertViolation().atLine(57).withMessage(getFormattedMessage("foobar", 8));
    assertViolation().atLine(62).withMessage(getFormattedMessage("a", 26));
    assertViolation().atLine(66).withMessage(getFormattedMessage("foobar", 8));
    assertViolation().atLine(67).withMessage(getFormattedMessage("fileA", 11));
    assertViolation().atLine(71).withMessage(getFormattedMessage("a", 26));
    assertViolation().atLine(72).withMessage(getFormattedMessage("fileA", 11));
    assertViolation().atLine(80).withMessage(getFormattedMessage("b", 27));
    assertViolation().atLine(94).withMessage(getFormattedMessage("i", 92));
    assertViolation().atLine(108).withMessage(getFormattedMessage("a", 26));
  }
}
