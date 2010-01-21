/*
 * Copyright (C) 2010 Evgeny Mandrikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonar.plugins.scmactivity;

import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.manager.ExtScmManager;
import org.apache.maven.scm.repository.ScmRepository;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.test.IsMeasure;
import org.sonar.api.test.IsResource;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.sonar.api.resources.Resource.QUALIFIER_CLASS;
import static org.sonar.api.resources.Resource.SCOPE_ENTITY;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivitySensorTest {
  private ScmActivitySensor sensor;

  @Before
  public void setUp() {
    sensor = new ScmActivitySensor();
  }

  @Test
  public void testShouldExecuteOnProject() throws Exception {
    Project project = mock(Project.class);
    MavenProject mavenProject = mock(MavenProject.class);
    when(project.getProperty(ScmActivitySensor.ENABLED_PROPERTY)).thenReturn(true, false, true);
    when(project.getPom()).thenReturn(mavenProject);
    when(mavenProject.getScm()).thenReturn(null).thenReturn(new Scm());

    assertFalse(sensor.shouldExecuteOnProject(project));
    assertFalse(sensor.shouldExecuteOnProject(project));
    assertTrue(sensor.shouldExecuteOnProject(project));
  }

  @Test
  public void testAnalyzeBlame() throws Exception {
    SensorContext context;
    context = mock(SensorContext.class);

    List<String> revisions = Arrays.asList("2", "1");
    List<String> authors = Arrays.asList("godin", "godin");
    List<Date> dates = Arrays.asList(new Date(13), new Date(10));
    ExtScmManager scmManager = mock(ExtScmManager.class);
    when(
        scmManager.blame((ScmRepository) any(), (ScmFileSet) any(), (String) any())
    ).thenReturn(
        new BlameScmResult("fake", authors, dates, revisions)
    );

    sensor.analyzeBlame(scmManager, null, new File("."), null, context, new JavaFile("org.example.HelloWorld"));

    verify(context).saveMeasure(
        argThat(new IsResource(SCOPE_ENTITY, QUALIFIER_CLASS, "org.example.HelloWorld")),
        argThat(new IsMeasure(ScmActivityMetrics.LAST_ACTIVITY, ScmActivitySensor.formatLastActivity(new Date(13))))
    );
    verify(context).saveMeasure(
        argThat(new IsResource(SCOPE_ENTITY, QUALIFIER_CLASS, "org.example.HelloWorld")),
        argThat(new IsMeasure(ScmActivityMetrics.REVISION, "2"))
    );
  }
} 
