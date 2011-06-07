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

package org.sonar.plugins.csharp.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Properties;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.api.batch.bootstrap.ProjectDefinition;
import org.sonar.api.batch.bootstrap.ProjectReactor;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.csharp.api.CSharpConfiguration;
import org.sonar.plugins.csharp.api.CSharpConstants;
import org.sonar.plugins.csharp.api.MicrosoftWindowsEnvironment;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioSolution;

public class VisualStudioProjectBuilderTest {

  private static File fakeSdkDir;
  private static File fakeSilverlightDir;
  private ProjectReactor reactor;
  private ProjectDefinition root;
  private static MicrosoftWindowsEnvironment microsoftWindowsEnvironment;
  private VisualStudioProjectBuilder projectBuilder;
  private Configuration conf;

  @BeforeClass
  public static void initResources() {
    fakeSdkDir = new File("Sonar/SDK");
    fakeSdkDir.mkdirs();
    fakeSilverlightDir = new File("Sonar/Silverlight");
    fakeSilverlightDir.mkdirs();
    microsoftWindowsEnvironment = new MicrosoftWindowsEnvironment();
  }

  @AfterClass
  public static void removeResources() {
    fakeSdkDir.delete();
    fakeSilverlightDir.delete();
  }

  @Before
  public void initBuilder() {
    conf = new BaseConfiguration();
    conf.addProperty("sonar.language", "cs");
    conf.addProperty(CSharpConstants.DOTNET_4_0_SDK_DIR_KEY, fakeSdkDir.getAbsolutePath());
    conf.addProperty(CSharpConstants.SILVERLIGHT_4_MSCORLIB_LOCATION_KEY, fakeSilverlightDir.getAbsolutePath());
    root = ProjectDefinition.create(new Properties()).setBaseDir(FileUtils.toFile(getClass().getResource("/solution/Example")))
        .setWorkDir(new File("target/sonar/.sonar"));
    root.setVersion("1.0");
    root.setKey("groupId:artifactId");
    reactor = new ProjectReactor(root);
    projectBuilder = new VisualStudioProjectBuilder(reactor, new CSharpConfiguration(conf), microsoftWindowsEnvironment);
  }

  @Test(expected = SonarException.class)
  public void testNotValidSdkDir() throws Exception {
    conf = new BaseConfiguration();
    conf.addProperty("sonar.language", "cs");
    conf.addProperty(CSharpConstants.DOTNET_4_0_SDK_DIR_KEY, "foo");
    projectBuilder = new VisualStudioProjectBuilder(reactor, new CSharpConfiguration(conf), microsoftWindowsEnvironment);
    projectBuilder.build(reactor);
  }

  @Test(expected = SonarException.class)
  public void testNotValidSilverlightDir() throws Exception {
    conf = new BaseConfiguration();
    conf.addProperty("sonar.language", "cs");
    conf.addProperty(CSharpConstants.SILVERLIGHT_4_MSCORLIB_LOCATION_KEY, "foo");
    projectBuilder = new VisualStudioProjectBuilder(reactor, new CSharpConfiguration(conf), microsoftWindowsEnvironment);
    projectBuilder.build(reactor);
  }

  @Test(expected = SonarException.class)
  public void testNonExistingSlnFile() throws Exception {
    conf.addProperty(CSharpConstants.SOLUTION_FILE_KEY, "NonExistingFile.sln");
    projectBuilder.build(reactor);
  }

  @Test
  public void testCorrectlyConfiguredProject() throws Exception {
    conf.addProperty(CSharpConstants.SOLUTION_FILE_KEY, "Example.sln");
    projectBuilder.build(reactor);
    // check that the configuration is OK
    assertThat(microsoftWindowsEnvironment.getDotnetVersion(), is("4.0"));
    assertThat(microsoftWindowsEnvironment.getDotnetSdkDirectory().getAbsolutePath(), is(fakeSdkDir.getAbsolutePath()));
    assertThat(microsoftWindowsEnvironment.getSilverlightVersion(), is("4"));
    assertThat(microsoftWindowsEnvironment.getSilverlightDirectory().getAbsolutePath(), is(fakeSilverlightDir.getAbsolutePath()));
    // check that the solution is built
    VisualStudioSolution solution = microsoftWindowsEnvironment.getCurrentSolution();
    assertNotNull(solution);
    assertThat(solution.getProjects().size(), is(3));
    // check the multi-module definition is correct
    assertThat(reactor.getRoot().getSubProjects().size(), is(2));
    assertThat(reactor.getRoot().getSourceFiles().size(), is(0));
    assertThat(microsoftWindowsEnvironment.getCurrentProject("Example.Core").getSourceFiles().size(), is(6));
    assertThat(microsoftWindowsEnvironment.getCurrentProject("Example.Application").getSourceFiles().size(), is(2));
  }

  @Test
  public void testNoSpecifiedSlnFileButOneFound() throws Exception {
    conf.addProperty(CSharpConstants.SOLUTION_FILE_KEY, "");
    projectBuilder = new VisualStudioProjectBuilder(reactor, new CSharpConfiguration(conf), new MicrosoftWindowsEnvironment());
    projectBuilder.build(reactor);
    assertThat(microsoftWindowsEnvironment.getDotnetSdkDirectory().getAbsolutePath(), is(fakeSdkDir.getAbsolutePath()));
    VisualStudioSolution solution = microsoftWindowsEnvironment.getCurrentSolution();
    assertNotNull(solution);
    assertThat(solution.getProjects().size(), is(3));
  }

  @Test(expected = SonarException.class)
  public void testNoSpecifiedSlnFileButNoneFound() throws Exception {
    conf.addProperty(CSharpConstants.SOLUTION_FILE_KEY, "");
    root.setBaseDir(FileUtils.toFile(getClass().getResource("/solution")));
    projectBuilder.build(reactor);
  }

  @Test(expected = SonarException.class)
  public void testNoSpecifiedSlnFileButTooManyFound() throws Exception {
    conf.addProperty(CSharpConstants.SOLUTION_FILE_KEY, "");
    root.setBaseDir(FileUtils.toFile(getClass().getResource("/solution/FakeSolutionWithTwoSlnFiles")));
    projectBuilder.build(reactor);
  }

}
