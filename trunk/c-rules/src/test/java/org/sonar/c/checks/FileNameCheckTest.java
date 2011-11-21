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

public class FileNameCheckTest {

  @Test
  public void testCheckWithDefaultSettings() {
    setCurrentSourceFile(scanFile("/checks/fileName.cc", new FileNameCheck()));
    
    assertOnlyOneViolation().withMessage("The file name does not conform to the specified format: ^([a-z0-9]|-|_)*\\.(c|h)$");
  }

  @Test
  public void testCheckWithSpecificFormat() {
    FileNameCheck check = new FileNameCheck();
    check.setFileNameFormat("^[a-zA-Z0-9]*\\.cpp$");
    
    setCurrentSourceFile(scanFile("/checks/fileName.cc", check));
    
    assertOnlyOneViolation().withMessage("The file name does not conform to the specified format: ^[a-zA-Z0-9]*\\.cpp$");
  }
  
  @Test
  public void testCheckWithSpecificFormatOk() {
    FileNameCheck check = new FileNameCheck();
    check.setFileNameFormat("^[a-zA-Z0-9]*\\.c$");
    
    setCurrentSourceFile(scanFile("/checks/fileName.c", check));
    
    assertNoViolation();
  }
  
}
