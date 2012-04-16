/*
 * Sonar Flex Plugin
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

package org.sonar.plugins.flex.flexmetrics;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;
import org.sonar.plugins.flex.core.Flex;

public class FlexMetricsSensorTest {

  private FlexMetricsSensor sensor;
  private Project project;

  @Before
  public void init() {
    project = mock(Project.class);
    sensor = new FlexMetricsSensor(null, null);
  }

  @Test
  public void shouldReturnMavenPluginHandler() {
    FlexMetricsMavenPluginHandler mavenPluginHandler = mock(FlexMetricsMavenPluginHandler.class);
    sensor = new FlexMetricsSensor(mavenPluginHandler, null);

    assertThat(sensor.getMavenPluginHandler(project), is((MavenPluginHandler) mavenPluginHandler));
  }

  @Test
  public void shouldExecuteOnProject() {
    when(project.getLanguageKey()).thenReturn(Flex.KEY);

    assertThat(sensor.shouldExecuteOnProject(project), is(true));
  }

  @Test
  public void shouldNotExecuteOnProject() {
    when(project.getLanguageKey()).thenReturn(Java.KEY);

    assertThat(sensor.shouldExecuteOnProject(project), is(false));
  }
}
