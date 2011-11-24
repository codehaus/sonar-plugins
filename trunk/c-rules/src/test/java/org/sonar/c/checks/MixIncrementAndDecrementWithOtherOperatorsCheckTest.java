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

public class MixIncrementAndDecrementWithOtherOperatorsCheckTest {

  @Test
  public void testCheck() {
    setCurrentSourceFile(scanFile("/checks/mixIncrementAndDecrementWithOtherOperators.c",
        new MixIncrementAndDecrementWithOtherOperatorsCheck()));

    assertNumberOfViolations(29);

    assertViolation().atLine(21).withMessage(
        "The increment (++) and decrement (--) operators shall not be mixed with other operators in an expression.");
    assertViolation().atLine(28);
    assertViolation().atLine(29);
    assertViolation().atLine(30);
    assertViolation().atLine(31);
    assertViolation().atLine(34);
    assertViolation().atLine(37);
    assertViolation().atLine(49);
    assertViolation().atLine(51);
    assertViolation().atLine(53);
    assertViolation().atLine(54);
    assertViolation().atLine(55);
    assertViolation().atLine(57);
    assertViolation().atLine(58);
    assertViolation().atLine(59);
    assertViolation().atLine(61);
    assertViolation().atLine(62);
    assertViolation().atLine(63);
    assertViolation().atLine(65);
    assertViolation().atLine(66);
    assertViolation().atLine(67);
    assertViolation().atLine(69);
    assertViolation().atLine(70);
    assertViolation().atLine(71);
    assertViolation().atLine(72);
    assertViolation().atLine(73);
    assertViolation().atLine(74);
    assertViolation().atLine(84);
    assertViolation().atLine(85);
  }
}
