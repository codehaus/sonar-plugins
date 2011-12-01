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

    assertNumberOfViolations(17);

    assertViolation().atLine(13).withMessage(getFormattedMessage("fileA", 11));
    assertViolation().atLine(49).withMessage(getFormattedMessage("errno", 10));
    assertViolation().atLine(52).withMessage(getFormattedMessage("fileA", 11));
    assertViolation().atLine(63).withMessage(getFormattedMessage("fileA", 11));
    assertViolation().atLine(66).withMessage(getFormattedMessage("a", 46));
    assertViolation().atLine(74).withMessage(getFormattedMessage("a", 46));
    assertViolation().atLine(77).withMessage(getFormattedMessage("foobar", 8));
    assertViolation().atLine(82).withMessage(getFormattedMessage("a", 46));
    assertViolation().atLine(86).withMessage(getFormattedMessage("foobar", 8));
    assertViolation().atLine(87).withMessage(getFormattedMessage("fileA", 11));
    assertViolation().atLine(91).withMessage(getFormattedMessage("a", 46));
    assertViolation().atLine(92).withMessage(getFormattedMessage("fileA", 11));
    assertViolation().atLine(100).withMessage(getFormattedMessage("b", 47));
    assertViolation().atLine(114).withMessage(getFormattedMessage("i", 112));
    assertViolation().atLine(128).withMessage(getFormattedMessage("a", 46));
    assertViolation().atLine(136).withMessage(getFormattedMessage("argc", 41));
    assertViolation().atLine(137).withMessage(getFormattedMessage("argv", 41));
  }

}
