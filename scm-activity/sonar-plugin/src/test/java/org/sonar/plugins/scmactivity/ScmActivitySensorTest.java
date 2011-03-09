/*
 * Sonar SCM Activity Plugin
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

package org.sonar.plugins.scmactivity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sonar.api.resources.Project;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivitySensorTest {

  private ScmActivitySensor sensor;
  private ProjectScmManager scmManager;

  @Before
  public void setUp() {
    scmManager = mock(ProjectScmManager.class);
    sensor = new ScmActivitySensor(scmManager, null);
  }

  /**
   * See SONARPLUGINS-350
   */
  @Test
  @Ignore
  public void noExecutionIfNotLatestAnalysis() {

  }

  @Test
  public void shouldExecuteOnProject() {
    when(scmManager.isEnabled()).thenReturn(true);
    assertThat(sensor.shouldExecuteOnProject(new Project("foo")), is(true));
    verify(scmManager).isEnabled();
  }

  @Test
  public void testToString() {
    assertThat(sensor.toString(), is("ScmActivitySensor"));
  }
}
