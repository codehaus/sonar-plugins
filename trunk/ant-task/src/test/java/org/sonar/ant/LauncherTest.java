/*
 * Sonar Ant Task
 * Copyright (C) 2011 SonarSource
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

package org.sonar.ant;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Environment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.CoreProperties;
import org.sonar.batch.bootstrapper.ProjectDefinition;
import org.sonar.test.TestUtils;

import java.io.File;
import java.util.Properties;

import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LauncherTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private Project antProject;
  private SonarTask task;
  private Launcher launcher;

  @Before
  public void setUp() {
    antProject = new Project();
    antProject.setBaseDir(new File("."));
    task = new SonarTask();
    task.setProject(antProject);
    launcher = new Launcher(task);
  }

  @Test
  public void defaultValues() {
    antProject.setName("My project");
    antProject.setDescription("My description");
    task.setKey("org.example:example");
    task.setVersion("0.1-SNAPSHOT");

    ProjectDefinition sonarProject = launcher.defineProject();

    assertThat(sonarProject.getBaseDir(), is(antProject.getBaseDir()));
    assertThat(sonarProject.getWorkDir(), is(task.getWorkDir()));
    Properties sonarProperties = sonarProject.getProperties();
    assertThat(sonarProperties.getProperty(CoreProperties.PROJECT_KEY_PROPERTY), is("org.example:example"));
    assertThat(sonarProperties.getProperty(CoreProperties.PROJECT_VERSION_PROPERTY), is("0.1-SNAPSHOT"));
    assertThat(sonarProperties.getProperty(CoreProperties.PROJECT_NAME_PROPERTY), is("My project"));
    assertThat(sonarProperties.getProperty(CoreProperties.PROJECT_DESCRIPTION_PROPERTY), is("My description"));
  }

  @Test
  public void overrideDefaultValues() {
    antProject.setProperty(CoreProperties.PROJECT_BRANCH_PROPERTY, "branch");
    task.setKey("org.example:example");
    task.setVersion("0.1-SNAPSHOT");
    File newBaseDir = new File("newBaseDir");
    task.setBaseDir(newBaseDir);

    setProperty(task, CoreProperties.PROJECT_NAME_PROPERTY, "My project");
    setProperty(task, CoreProperties.PROJECT_DESCRIPTION_PROPERTY, "My description");
    setProperty(task, CoreProperties.PROJECT_BRANCH_PROPERTY, "Not used");

    ProjectDefinition sonarProject = launcher.defineProject();

    Properties sonarProperties = sonarProject.getProperties();
    assertThat(sonarProperties.getProperty(CoreProperties.PROJECT_KEY_PROPERTY), is("org.example:example"));
    assertThat(sonarProperties.getProperty(CoreProperties.PROJECT_VERSION_PROPERTY), is("0.1-SNAPSHOT"));
    assertThat(sonarProperties.getProperty(CoreProperties.PROJECT_NAME_PROPERTY), is("My project"));
    assertThat(sonarProperties.getProperty(CoreProperties.PROJECT_DESCRIPTION_PROPERTY), is("My description"));
    assertThat(sonarProperties.getProperty(CoreProperties.PROJECT_BRANCH_PROPERTY), is("branch"));
    assertThat(sonarProject.getBaseDir(), is(newBaseDir));
  }

  @Test
  public void defaultLogLevelShouldBeInfo() {
    assertThat(launcher.getLoggerLevel(new PropertiesConfiguration()), is("INFO"));
  }

  @Test
  public void shouldEnableVerboseMode() {
    PropertiesConfiguration config = new PropertiesConfiguration();
    config.setProperty("sonar.verbose", "true");
    assertThat(launcher.getLoggerLevel(config), is("DEBUG"));
  }

  @Test
  public void shouldDisableVerboseMode() {
    PropertiesConfiguration config = new PropertiesConfiguration();
    config.setProperty("sonar.verbose", "false");
    assertThat(launcher.getLoggerLevel(config), is("INFO"));
  }

  private void setProperty(SonarTask task, String key, String value) {
    Environment.Variable var = new Environment.Variable();
    var.setKey(key);
    var.setValue(value);
    task.addConfiguredProperty(var);
  }

  @Test
  public void testGetSqlLevel() throws Exception {
    Configuration conf = new BaseConfiguration();

    assertThat(Launcher.getSqlLevel(conf), is("WARN"));

    conf.setProperty("sonar.showSql", "true");
    assertThat(Launcher.getSqlLevel(conf), is("DEBUG"));

    conf.setProperty("sonar.showSql", "false");
    assertThat(Launcher.getSqlLevel(conf), is("WARN"));
  }

  @Test
  public void testGetSqlResultsLevel() throws Exception {
    Configuration conf = new BaseConfiguration();

    assertThat(Launcher.getSqlResultsLevel(conf), is("WARN"));

    conf.setProperty("sonar.showSqlResults", "true");
    assertThat(Launcher.getSqlResultsLevel(conf), is("DEBUG"));

    conf.setProperty("sonar.showSqlResults", "false");
    assertThat(Launcher.getSqlResultsLevel(conf), is("WARN"));
  }

  @Test
  public void shouldFailIfMandatoryPropertiesMissing() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The following mandatory information is missing:");
    thrown.expectMessage("- property 'sonar.sources'");
    thrown.expectMessage("- property 'sonar.projectKey'");

    Launcher.checkAntProjectForMandatoryProperties(new Project());
  }

  @Test
  public void shouldNotFailIfMandatoryPropertiesPresent() {
    Project antProject = new Project();
    antProject.setProperty("sonar.sources", "src");
    antProject.setProperty("sonar.projectKey", "foo");

    Launcher.checkAntProjectForMandatoryProperties(antProject);
  }

  @Test
  public void shouldFindSubModuleBuildFileWithModuleAbsolutePath() {
    File buildFile = TestUtils.getResource("org/sonar/ant/LauncherTest/build.xml");

    File foundFile = Launcher.findSubModuleBuildFile(new Project(), buildFile.getAbsolutePath());
    assertThat(foundFile, is(buildFile));
  }

  @Test
  public void shouldFindSubModuleBuildFileWithModuleRelativePath() {
    Project antProject = new Project();
    antProject.setBaseDir(TestUtils.getResource("org/sonar/ant"));

    File foundFile = Launcher.findSubModuleBuildFile(antProject, "LauncherTest/build.xml");
    assertThat(foundFile, is(TestUtils.getResource("org/sonar/ant/LauncherTest/build.xml")));
  }

  @Test
  public void shouldCopyMissingPropertiesButNotSonarModules() throws Exception {
    antProject.setProperty("basedir", "/tmp/foo");
    antProject.setProperty("ant.file", "my-build.xml");
    antProject.setProperty("sonar.sources", "src/main/js");
    antProject.setProperty("sonar.tests", "src/test/js");
    antProject.setProperty("sonar.modules", "foo, bar");

    Project subProject = new Project();
    subProject.setProperty("sonar.sources", "src/main/java");

    launcher.copyMissingProperties(antProject, subProject);
    assertThat(subProject.getProperty("basedir")).isNull();
    assertThat(subProject.getProperty("ant.file")).isNull();
    assertThat(subProject.getProperty("sonar.modules")).isNull();
    assertThat(subProject.getProperty("sonar.sources")).isEqualTo("src/main/java");
    assertThat(subProject.getProperty("sonar.tests")).isEqualTo("src/test/js");
  }
}
