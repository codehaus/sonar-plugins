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

import org.junit.Test;

public class ForLoopCounterChangedCheckTest {

  @Test
  public void testCheck() {
    setCurrentSourceFile(scanFile("/checks/forLoopCounterChanged.c", new ForLoopCounterChangedCheck()));

    assertNumberOfViolations(12);

    assertViolation().atLine(14).withMessage(getFormattedMessage("x", 12));
    assertViolation().atLine(19).withMessage(getFormattedMessage("x", 17));
    assertViolation().atLine(24).withMessage(getFormattedMessage("x", 22));
    assertViolation().atLine(29).withMessage(getFormattedMessage("x", 27));
    assertViolation().atLine(34).withMessage(getFormattedMessage("x", 32));
    assertViolation().atLine(40).withMessage(getFormattedMessage("x", 38));
    assertViolation().atLine(45).withMessage(getFormattedMessage("x", 43));
    assertViolation().atLine(51).withMessage(getFormattedMessage("x", 49));
    assertViolation().atLine(56).withMessage(getFormattedMessage("x", 54));
    assertViolation().atLine(64).withMessage(getFormattedMessage("y", 62));
    assertViolation().atLine(72).withMessage(getFormattedMessage("x", 68));
    assertViolation().atLine(88).withMessage(getFormattedMessage("x", 86));
  }

  private String getFormattedMessage(String loopCounter, int line) {
    return "The loop counter variable \"" + loopCounter + "\" defined at line " + line + " shall not be changed in the loop body.";
  }

}
