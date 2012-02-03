/*
 * Sonar Python Plugin
 * Copyright (C) 2011 Waleri Enns
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

package org.sonar.plugins.python;

import static org.junit.Assert.assertEquals;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;
import java.util.LinkedList;

import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.apache.commons.configuration.Configuration;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;

import org.junit.Test;
import org.junit.Before;


public class PythonViolationsSensorTest {
  private PythonViolationsSensor sensor;
  private Project project;
  private ProjectFileSystem pfs;
  private RuleFinder ruleFinder;
  private Configuration conf;
  private RulesProfile profile;
  
  @Before
  public void init() {
    ruleFinder = mock(RuleFinder.class);
    conf = mock(Configuration.class);
    profile = mock(RulesProfile.class);

    pfs = mock(ProjectFileSystem.class);
    when(pfs.getBasedir()).thenReturn(new File("/tmp"));

    project = mock(Project.class);
    when(project.getFileSystem()).thenReturn(pfs);
  }

  @Test
  public void shouldntThrowWhenInstantiating() {
    new PythonViolationsSensor(ruleFinder, project, conf, profile);
  }
  
  @Test
  public void shouldReturnCorrectEnvironmentIfPropertySet() {
    when(project.getProperty("sonar.python.path")).thenReturn("path1,path2");
    String[] env = PythonViolationsSensor.getEnvironment(project);

    String[] expectedEnv = {"PYTHONPATH=/tmp/path1:/tmp/path2"};
    assertEquals(env, expectedEnv);
  }

  @Test
  public void shouldReturnNullIfPropertyNotSet() {
    String[] env = PythonViolationsSensor.getEnvironment(project);

    assertEquals(env, null);
  }

  @Test
  public void shouldExecuteOnlyWhenNecessary() {
    // which means: only on python projects and only if
    // there is at least one active pylint rule
  
    Project pythonProject = createProjectForLanguage(Python.KEY);
    Project foreignProject = createProjectForLanguage("whatever");
    RulesProfile emptyProfile = mock(RulesProfile.class);
    RulesProfile pylintProfile =  createPylintProfile();
    
    checkNecessityOfExecution(pythonProject, pylintProfile, true);
    checkNecessityOfExecution(pythonProject, emptyProfile, false);
    checkNecessityOfExecution(foreignProject, pylintProfile, false);
    checkNecessityOfExecution(foreignProject, emptyProfile, false);
  }
  
  private void checkNecessityOfExecution(Project project, RulesProfile profile,
                                         boolean shouldExecute){
    PythonViolationsSensor sensor =
      new PythonViolationsSensor(ruleFinder, project, conf, profile);
    assertEquals(sensor.shouldExecuteOnProject(project), shouldExecute);
  }

  private static Project createProjectForLanguage(String languageKey){
    Project project = mock(Project.class);
    when(project.getLanguageKey()).thenReturn(languageKey);
    return project;
  }

  private static RulesProfile createPylintProfile(){
    List<ActiveRule> rules = new LinkedList<ActiveRule>();
    rules.add(mock(ActiveRule.class));
    
    RulesProfile profile = mock(RulesProfile.class);
    when(profile.getActiveRulesByRepository(PythonRuleRepository.REPOSITORY_KEY))
      .thenReturn(rules);

    return profile;
  }
}
