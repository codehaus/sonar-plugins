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

public class FunctionNameCheckTest {

  @Test
  public void testCheckWithDefaultSettings() {
    setCurrentSourceFile(scanFile("/checks/functionName.c", new FunctionNameCheck()));

    assertOnlyOneViolation().atLine(3).withMessage("The function name does not conform to the specified format: ^[a-z][a-zA-Z0-9]*$");
  }

  @Test
  public void testCheckWithSpecificFormat() {
    FunctionNameCheck check = new FunctionNameCheck();
    check.setFunctionNameFormat("^[0-9][a-zA-Z0-9]*$");

    setCurrentSourceFile(scanFile("/checks/functionName.c", check));

    assertNumberOfViolations(2);

    assertViolation().atLine(3).withMessage("The function name does not conform to the specified format: ^[0-9][a-zA-Z0-9]*$");
    assertViolation().atLine(8).withMessage("The function name does not conform to the specified format: ^[0-9][a-zA-Z0-9]*$");
  }
}
