/*
 * Sonar C# Plugin :: FxCop
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

package org.sonar.plugins.csharp.fxcop.runner;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.csharp.fxcop.FxCopConstants;
import org.sonar.plugins.csharp.fxcop.runner.FxCopCommand;


public class FxCopCommandTest {

  private static FxCopCommand fxCopCommand;
  private static Configuration configuration;
  private static ProjectFileSystem projectFileSystem;
  private static File fakeFxCopProgramFile;

  @BeforeClass
  public static void initStatic() throws Exception {
    fakeFxCopProgramFile = FileUtils.toFile(FxCopCommandTest.class.getResource("/Runner/FakeFxCopConfigFile.xml"));
    configuration = new BaseConfiguration();
    projectFileSystem = mock(ProjectFileSystem.class);
    when(projectFileSystem.getBasedir()).thenReturn(FileUtils.toFile(FxCopCommandTest.class.getResource("/Runner")));
  }

  @Before
  public void init() throws Exception {
    configuration.clear();
    configuration.addProperty(FxCopConstants.EXECUTABLE_KEY, fakeFxCopProgramFile.getAbsolutePath());
  }

  @Test
  public void testToArray() throws Exception {
    configuration.addProperty(FxCopConstants.ASSEMBLIES_TO_SCAN_KEY, "FakeAssemblies/Fake1.assembly, FakeAssemblies/Fake2.assembly");
    configuration.addProperty(FxCopConstants.ASSEMBLIES_TO_SCAN_KEY, "FakeDepFolder, UnexistingFolder");
    fxCopCommand = new FxCopCommand(configuration, projectFileSystem);
    fxCopCommand.setFxCopConfigFile(fakeFxCopProgramFile);
    String[] commands = fxCopCommand.toArray();
    assertThat(commands[1], endsWith("FakeFxCopConfigFile.xml"));
    assertThat(commands[2], endsWith("fxcop-report.xml"));
    assertThat(commands[3], endsWith("Fake1.assembly"));
    assertThat(commands[4], endsWith("Fake2.assembly"));
    assertThat(commands[5], endsWith("FakeDepFolder"));
    assertThat(commands[6], endsWith("/igc"));
    assertThat(commands[7], endsWith("/to:600"));
    assertThat(commands[8], endsWith("/gac"));
  }

  @Test
  public void testToArrayWithOtherCustomParams() throws Exception {
    configuration.addProperty(FxCopConstants.ASSEMBLIES_TO_SCAN_KEY, "FakeAssemblies/Fake1.assembly, FakeAssemblies/Fake2.assembly");
    configuration.addProperty(FxCopConstants.IGNORE_GENERATED_CODE_KEY, "false");
    configuration.addProperty(FxCopConstants.TIMEOUT_MINUTES_KEY, "100");
    fxCopCommand = new FxCopCommand(configuration, projectFileSystem);
    fxCopCommand.setFxCopConfigFile(fakeFxCopProgramFile);
    String[] commands = fxCopCommand.toArray();
    assertThat(commands[1], endsWith("FakeFxCopConfigFile.xml"));
    assertThat(commands[2], endsWith("fxcop-report.xml"));
    assertThat(commands[3], endsWith("Fake1.assembly"));
    assertThat(commands[4], endsWith("Fake2.assembly"));
    assertThat(commands[5], endsWith("/to:6000"));
    assertThat(commands[6], endsWith("/gac"));
  }

  @Test(expected = IllegalStateException.class)
  public void testWithNoAssembly() throws Exception {
    fxCopCommand = new FxCopCommand(configuration, projectFileSystem);
    fxCopCommand.setFxCopConfigFile(fakeFxCopProgramFile);
    fxCopCommand.toArray();
  }

  @Test(expected = IllegalStateException.class)
  public void testWithNullFxCopConfigFile() throws Exception {
    fxCopCommand = new FxCopCommand(configuration, projectFileSystem);
    fxCopCommand.toArray();
  }

}
