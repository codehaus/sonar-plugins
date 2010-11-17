/*
 * Technical Debt Sonar plugin
 * Copyright (C) 2009 SonarSource
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

package org.sonar.plugins.technicaldebt;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.Sensor;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Project;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComplexityDebtSensorTest {

  private ComplexityDebtSensor sensor;

  @Before
  public void setUp() {
    sensor = new ComplexityDebtSensor(null, null);
  }

  @Test
  public void dependsUponSquidAanalysis() {
    assertThat(sensor.dependsUpon(), Matchers.hasItem(Sensor.FLAG_SQUID_ANALYSIS));
  }

  @Test
  public void shouldExecuteOnlyOnJavaProject() {
    Project project = mock(Project.class);
    Language anotherLanguage = mock(Language.class);
    when(project.getLanguage()).thenReturn(Java.INSTANCE).thenReturn(anotherLanguage);

    assertThat(sensor.shouldExecuteOnProject(project), is(true));
    assertThat(sensor.shouldExecuteOnProject(project), is(false));
  }

}
