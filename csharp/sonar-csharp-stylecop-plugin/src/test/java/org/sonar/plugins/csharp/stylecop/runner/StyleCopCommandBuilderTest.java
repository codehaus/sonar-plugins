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
import org.sonar.plugins.csharp.api.utils.Command;

public class StyleCopCommandBuilderTest {

  private static StyleCopCommandBuilder styleCopCommandBuilder;
  private static ProjectFileSystem projectFileSystem;

  @BeforeClass
  public static void initStatic() throws Exception {
    projectFileSystem = mock(ProjectFileSystem.class);
    when(projectFileSystem.getBasedir()).thenReturn(FileUtils.toFile(StyleCopCommandBuilderTest.class.getResource("/Runner")));
  }

  @Test
  public void testToArray() throws Exception {
    styleCopCommandBuilder = new StyleCopCommandBuilder(projectFileSystem);
    styleCopCommandBuilder.setStyleCopConfigFile(FileUtils.toFile(getClass().getResource("/Runner/Command/SimpleRules.StyleCop")));
    styleCopCommandBuilder.setMsBuildFile(FileUtils.toFile(getClass().getResource("/Runner/Command/stylecop-msbuild.xml")));
    styleCopCommandBuilder.setDotnetSdkDirectory(new File("FakeDirectory"));
    Command command = styleCopCommandBuilder.createCommand();

    assertThat(command.getExecutable(), endsWith("MSBuild.exe"));
    String[] commands = command.getArguments().toArray(new String[] {});
    assertThat(commands[0], endsWith("Runner"));
    assertThat(commands[1], is("/target:StyleCopLaunch"));
    assertThat(commands[2], is("/noconsolelogger"));
    assertThat(commands[3], endsWith("stylecop-msbuild.xml"));
  }

  @Test(expected = IllegalStateException.class)
  public void testWithUnexistingStyleCopConfigFile() throws Exception {
    styleCopCommandBuilder = new StyleCopCommandBuilder(projectFileSystem);
    styleCopCommandBuilder.setStyleCopConfigFile(new File("Fake"));
    styleCopCommandBuilder.createCommand();
  }

  @Test(expected = IllegalStateException.class)
  public void testWithUnexistingMsBuildFile() throws Exception {
    styleCopCommandBuilder = new StyleCopCommandBuilder(projectFileSystem);
    styleCopCommandBuilder.setStyleCopConfigFile(FileUtils.toFile(getClass().getResource("/Runner/Command/SimpleRules.StyleCop")));
    styleCopCommandBuilder.setMsBuildFile(FileUtils.toFile(getClass().getResource("Fake")));
    styleCopCommandBuilder.createCommand();
  }

}
