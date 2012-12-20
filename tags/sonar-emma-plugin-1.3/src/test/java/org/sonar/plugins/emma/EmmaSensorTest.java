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
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Scopes;
import org.sonar.api.test.IsResource;
import org.sonar.api.test.MavenTestUtils;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class EmmaSensorTest {

  @Test
  public void should_load_report() {
    SensorContext context = mock(SensorContext.class);
    Project project = MavenTestUtils.loadProjectFromPom(getClass(), "project/pom.xml");

    EmmaSettings settings = mock(EmmaSettings.class);
    when(settings.getReportPath()).thenReturn("target/emma");
    new EmmaSensor(settings).analyse(project, context);

    verify(context).saveMeasure(
      argThat(new IsResource(Scopes.FILE, Qualifiers.CLASS, "org.apache.struts.util.MessageResourcesFactory")),
      eq(CoreMetrics.LINES_TO_COVER), anyDouble());
  }

  @Test
  public void should_do_nothing_if_no_report() {
    SensorContext context = mock(SensorContext.class);
    Project project = MavenTestUtils.loadProjectFromPom(getClass(), "project/pom.xml");

    EmmaSettings settings = mock(EmmaSettings.class);
    new EmmaSensor(settings).analyse(project, context);

    verifyZeroInteractions(context);
  }

  @Test
  public void should_not_fail_if_report_path_is_bad() {
    SensorContext context = mock(SensorContext.class);
    Project project = MavenTestUtils.loadProjectFromPom(getClass(), "project/pom.xml");
    EmmaSettings settings = mock(EmmaSettings.class);
    when(settings.getReportPath()).thenReturn("target/unknown");

    new EmmaSensor(settings).analyse(project, context);

    verifyZeroInteractions(context);
  }

  @Test
  public void should_not_fail_if_report_path_is_a_file() {
    SensorContext context = mock(SensorContext.class);
    Project project = MavenTestUtils.loadProjectFromPom(getClass(), "project/pom.xml");
    EmmaSettings settings = mock(EmmaSettings.class);
    when(settings.getReportPath()).thenReturn("target/emma/coverage-0.ec");

    new EmmaSensor(settings).analyse(project, context);

    verifyZeroInteractions(context);
  }

  @Test
  public void should_be_disabled() {
    EmmaSettings settings = mock(EmmaSettings.class);
    Project project = new Project("foo");
    when(settings.isEnabled(project)).thenReturn(false);

    assertThat(new EmmaSensor(settings).shouldExecuteOnProject(project)).isFalse();
  }

  @Test
  public void should_be_enabled() {
    EmmaSettings settings = mock(EmmaSettings.class);
    Project project = new Project("foo");
    when(settings.isEnabled(project)).thenReturn(true);

    assertThat(new EmmaSensor(settings).shouldExecuteOnProject(project)).isTrue();
  }

  @Test
  public void test_toString() {
    assertThat(new EmmaSensor(mock(EmmaSettings.class)).toString()).isEqualTo("EmmaSensor");
  }
}
