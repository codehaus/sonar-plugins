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

public class OctalConstantsAndEscapesShallNotBeUsedCheckTest {

  @Test
  public void testCheck() {
    setCurrentSourceFile(scanFile("/checks/octalConstantsAndEscapesShallNotBeUsed.c", new OctalConstantsAndEscapesShallNotBeUsedCheck()));
    
    assertNumberOfViolations(6);
    
    assertViolation().atLine(6).withMessage("Octal constants (other than zero) and octal escape sequences shall not be used.");
    assertViolation().atLine(8);
    assertViolation().atLine(9);
    assertViolation().atLine(15);
    assertViolation().atLine(16);
    assertViolation().atLine(17);
  }
}
