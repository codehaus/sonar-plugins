/*
 * Sonar C# Plugin :: StyleCop
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

package org.sonar.plugins.csharp.stylecop.runner;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.api.resources.ProjectFileSystem;

public class StyleCopCommandTest {

  private static StyleCopCommand styleCopCommand;
  private static ProjectFileSystem projectFileSystem;

  @BeforeClass
  public static void initStatic() throws Exception {
    projectFileSystem = mock(ProjectFileSystem.class);
    when(projectFileSystem.getBasedir()).thenReturn(FileUtils.toFile(StyleCopCommandTest.class.getResource("/Runner")));
  }

  @Test
  public void testToArray() throws Exception {
    styleCopCommand = new StyleCopCommand(projectFileSystem);
    styleCopCommand.setStyleCopConfigFile(FileUtils.toFile(getClass().getResource("/Runner/Command/SimpleRules.StyleCop")));
    styleCopCommand.setMsBuildFile(FileUtils.toFile(getClass().getResource("/Runner/Command/stylecop-msbuild.xml")));
    styleCopCommand.setDotnetSdkDirectory(new File("FakeDirectory"));
    String[] commands = styleCopCommand.toArray();
    assertThat(commands[0], endsWith("MSBuild.exe"));
    assertThat(commands[1], endsWith("Runner"));
    assertThat(commands[2], is("/target:StyleCopLaunch"));
    assertThat(commands[3], is("/noconsolelogger"));
    assertThat(commands[4], endsWith("stylecop-msbuild.xml"));
  }

  @Test(expected = IllegalStateException.class)
  public void testWithUnexistingStyleCopConfigFile() throws Exception {
    styleCopCommand = new StyleCopCommand(projectFileSystem);
    styleCopCommand.setStyleCopConfigFile(new File("Fake"));
    styleCopCommand.toArray();
  }

  @Test(expected = IllegalStateException.class)
  public void testWithUnexistingMsBuildFile() throws Exception {
    styleCopCommand = new StyleCopCommand(projectFileSystem);
    styleCopCommand.setStyleCopConfigFile(FileUtils.toFile(getClass().getResource("/Runner/Command/SimpleRules.StyleCop")));
    styleCopCommand.setMsBuildFile(FileUtils.toFile(getClass().getResource("Fake")));
    styleCopCommand.toArray();
  }

}
