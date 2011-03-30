/*
 * Sonar C# Plugin :: Core
 * Copyright (C) 2010 Jose Chillan, Alexandre Victoor and SonarSource
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

package org.sonar.plugins.csharp.api.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class CommandTest {

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailWhenBlankExecutable() throws Exception {
    Command.create("  ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailWhenNullExecutable() throws Exception {
    Command.create(null);
  }

  @Test
  public void shouldCreateCommand() throws Exception {
    Command command = Command.create("java");
    command.addArgument("-Xmx512m");
    command.addArgument("-Dfoo=bar");
    assertThat(command.getExecutable(), is("java"));
    assertThat(command.getArguments().size(), is(2));
    assertThat(command.toCommandLine(), is("java -Xmx512m -Dfoo=bar"));
  }
}
