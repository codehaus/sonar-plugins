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

public class ForLoopCounterCheckTest {

  @Test
  public void testCheck() {
    setCurrentSourceFile(scanFile("/checks/forLoopCounterCheck.c",
        new ForLoopCounterCheck()));

    assertNumberOfViolations(10);

    assertViolation().atLine(26).withMessage("The three expressions of a for statement shall be concerned only with loop control.");
    assertViolation().atLine(27);
    assertViolation().atLine(28);
    assertViolation().atLine(29);
    assertViolation().atLine(30);
    assertViolation().atLine(31);
    assertViolation().atLine(32);
    assertViolation().atLine(33);
    assertViolation().atLine(34);
    assertViolation().atLine(35);
  }
}
