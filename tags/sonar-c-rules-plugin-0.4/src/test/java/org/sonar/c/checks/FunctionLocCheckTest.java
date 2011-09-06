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
