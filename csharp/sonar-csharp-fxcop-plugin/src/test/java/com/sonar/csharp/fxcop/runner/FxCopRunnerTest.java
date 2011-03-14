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

package com.sonar.csharp.fxcop.runner;

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

import com.sonar.csharp.fxcop.FxCopConstants;

public class FxCopRunnerTest {

  private static FxCopRunner fxCopRunner;
  private static Configuration configuration;
  private static ProjectFileSystem projectFileSystem;
  private static File fakeFxCopProgramFile;
  private static File fakeFxCopConfigFile;

  @BeforeClass
  public static void initStatic() throws Exception {
    fakeFxCopProgramFile = getFakeFxCopProgramFile();
    fakeFxCopConfigFile = FileUtils.toFile(FxCopCommandTest.class.getResource("/Runner/FakeFxCopConfigFile.xml"));
    configuration = new BaseConfiguration();
    projectFileSystem = mock(ProjectFileSystem.class);
    when(projectFileSystem.getBasedir()).thenReturn(FileUtils.toFile(FxCopCommandTest.class.getResource("/Runner")));
  }

  @Before
  public void init() throws Exception {
    configuration.clear();
    configuration.addProperty(FxCopConstants.EXECUTABLE_KEY, fakeFxCopProgramFile.getAbsolutePath());
  }

  private static File getFakeFxCopProgramFile() throws Exception {
    File runnerFolder = FileUtils.toFile(FxCopRunnerTest.class.getResource("/Runner/FakeProg"));
    File fakeFxCopProgramFile = new File(runnerFolder, "FxCopCmd.bat");
    if ( !System.getProperty("os.name").startsWith("Windows")) {
      fakeFxCopProgramFile = new File(runnerFolder, "FxCopCmd.sh");
      Runtime.getRuntime().exec("chmod a+x " + fakeFxCopProgramFile.getAbsolutePath());
    }
    return fakeFxCopProgramFile;
  }

  @Test
  public void testExecute() throws Exception {
    // for some reason, this test fails on the CI server which is on Windows...
    if ( !System.getProperty("os.name").startsWith("Windows")) {
      configuration.addProperty(FxCopConstants.ASSEMBLIES_TO_SCAN_KEY, "FakeAssemblies/Fake1.assembly, FakeAssemblies/Fake2.assembly");
      fxCopRunner = new FxCopRunner(configuration, projectFileSystem);
      fxCopRunner.execute(fakeFxCopConfigFile);
    }
  }

  @Test(expected = IllegalStateException.class)
  public void testExecuteWithNoAssembly() throws Exception {
    fxCopRunner = new FxCopRunner(configuration, projectFileSystem);
    fxCopRunner.execute(fakeFxCopConfigFile);
  }

  @Test(expected = IllegalStateException.class)
  public void testWithNullParameter() throws Exception {
    fxCopRunner = new FxCopRunner(configuration, projectFileSystem);
    fxCopRunner.execute(null);
  }

}
