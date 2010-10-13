/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.c.checks;

import org.junit.Test;
import org.sonar.c.checks.FunctionLocCheck;
import org.sonar.squid.api.CheckMessage;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;

public class FunctionLocCheckTest {

  @Test
  public void testCheck() {
    FunctionLocCheck check = new FunctionLocCheck();
    check.setMaximumFunctionLocThreshold(3);
    CheckMessage message = CheckUtils.extractViolation("/checks/functionLoc.c", check);

    assertThat(message.getLine(), is(8));
    assertThat(message.formatDefaultMessage(), containsString("Function has 4 lines of code which is greater than 3 authorized."));
  }
}
