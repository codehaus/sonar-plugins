/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
 * mailto:contact AT sonarsource DOT com
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

package org.sonar.plugins.emma;

import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.test.IsResource;
import org.sonar.api.test.MavenTestUtils;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmmaSensorTest {

  @Test
  public void shouldGetReportPathFromProperty() {
    SensorContext context = mock(SensorContext.class);
    Project project = MavenTestUtils.loadProjectFromPom(getClass(), "shouldGetReportPathFromProperty/pom.xml");
    new EmmaSensor(null, null).analyse(project, context);
    verify(context).saveMeasure(
        argThat(new IsResource(Resource.SCOPE_ENTITY, Resource.QUALIFIER_CLASS, "org.apache.struts.util.MessageResourcesFactory")),
        eq(CoreMetrics.LINES_TO_COVER), anyDouble());
  }

  @Test
  public void doNotExecuteMavenPluginIfReuseReports() {
    Project project = mock(Project.class);
    when(project.getAnalysisType()).thenReturn(Project.AnalysisType.REUSE_REPORTS);
    assertThat(new EmmaSensor(null, new EmmaMavenPluginHandler()).getMavenPluginHandler(project), nullValue());
  }

  @Test
  public void doNotExecuteMavenPluginIfStaticAnalysis() {
    Project project = mock(Project.class);
    when(project.getAnalysisType()).thenReturn(Project.AnalysisType.STATIC);
    assertThat(new EmmaSensor(null, new EmmaMavenPluginHandler()).getMavenPluginHandler(project), nullValue());
  }

  @Test
  public void executeMavenPluginIfDynamicAnalysis() {
    Project project = mock(Project.class);
    when(project.getAnalysisType()).thenReturn(Project.AnalysisType.DYNAMIC);
    assertThat(new EmmaSensor(null, new EmmaMavenPluginHandler()).getMavenPluginHandler(project), not(nullValue()));
    assertThat(new EmmaSensor(null, new EmmaMavenPluginHandler()).getMavenPluginHandler(project).getArtifactId(), is("emma-maven-plugin"));
  }

}
