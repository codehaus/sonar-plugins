/*
 * Maven and Sonar plugin for .Net
 * Copyright (C) 2010 Jose Chillan and Alexandre Victoor
 * mailto: jose.chillan@codehaus.org or alexvictoor@codehaus.org
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

package org.sonar.plugin.dotnet.stylecop;

import static org.sonar.plugin.dotnet.stylecop.Constants.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.maven.dotnet.commons.project.VisualStudioUtils;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulesManager;
import org.sonar.api.rules.Violation;

public class StyleCopSensorTest {

  private StyleCopSensor sensor;
  private RulesProfile profile;
  private RulesManager rulesManager;
  private StyleCopPluginHandler pluginHandler;
  
  @Before
  public void setUp() {
    pluginHandler = mock(StyleCopPluginHandler.class);
    profile = mock(RulesProfile.class);
    rulesManager = mock(RulesManager.class);
    sensor = new StyleCopSensor(rulesManager, pluginHandler, profile);
  }
  
  
  @Test
  public void testShouldExecuteOnProject() {
    Project project = mock(Project.class);
    Configuration configuration =  mock(Configuration.class);
    when(project.getPackaging()).thenReturn("sln");
    when(project.getConfiguration()).thenReturn(configuration);
    assertTrue(sensor.shouldExecuteOnProject(project)); 
  }
  
  @Test
  public void testShouldExecuteOnProjectAndNoSlnProject() {
    Project project = mock(Project.class);
    Configuration configuration =  mock(Configuration.class);
    when(project.getPackaging()).thenReturn("pom");
    when(project.getConfiguration()).thenReturn(configuration);
    assertFalse(sensor.shouldExecuteOnProject(project)); 
  }
  
  @Test
  public void testShouldExecuteOnProjectWithSkip() {
    Project project = mock(Project.class);
    Configuration configuration =  mock(Configuration.class);
    when(configuration.getString(STYLECOP_MODE_KEY, STYLECOP_DEFAULT_MODE)).thenReturn(STYLECOP_SKIP_MODE);
    when(project.getPackaging()).thenReturn("sln");
    when(project.getConfiguration()).thenReturn(configuration);
    assertFalse(sensor.shouldExecuteOnProject(project)); 
  }
  
  
  private void testAnalyseReuse(String reportPathParam) {
    // set up maven project
    MavenProject mvnProject = new MavenProject();
    mvnProject.setPackaging("sln");
    mvnProject.getProperties().put(VisualStudioUtils.VISUAL_SOLUTION_NAME_PROPERTY, "Example.sln");
    File pomFile 
      = new File("target/test-classes/solution/Example/pom.xml");
    mvnProject.setFile(pomFile);
    
    // set up sonar project
    Project project = mock(Project.class);
    when(project.getPom()).thenReturn(mvnProject);
    Configuration configuration =  mock(Configuration.class);
    when(configuration.getString(STYLECOP_MODE_KEY, STYLECOP_DEFAULT_MODE)).thenReturn(STYLECOP_REUSE_MODE);
    when(configuration.getString(STYLECOP_REPORT_KEY)).thenReturn(reportPathParam);
    when(project.getConfiguration()).thenReturn(configuration);
    ProjectFileSystem projectFileSystem = mock(ProjectFileSystem.class);
    when(project.getFileSystem()).thenReturn(projectFileSystem);
    when(projectFileSystem.getBuildDir()).thenReturn(new File("target/test-classes/solution/Example/target"));
    
    // set up rules manager
    Rule dummyRule = mock(Rule.class);
    when(rulesManager.getPluginRule(eq(StyleCopPlugin.KEY), anyString())).thenReturn(dummyRule);
    
    
    SensorContext context = mock(SensorContext.class);
    
    sensor.analyse(project, context);
    
    verify(context,atLeastOnce()).saveViolation(any(Violation.class));
  }
  
  @Test
  public void testAnalyseReuseSameDir() {
    testAnalyseReuse("stylecop-alt-report.xml");
  }

  @Test
  public void testAnalyseReuseDifferentDir() {
    testAnalyseReuse("../working/stylecop-report.xml");
  }

  @Test
  @Ignore
  public void testGetMavenPluginHandler() {
    fail("Not yet implemented");
  }

}
