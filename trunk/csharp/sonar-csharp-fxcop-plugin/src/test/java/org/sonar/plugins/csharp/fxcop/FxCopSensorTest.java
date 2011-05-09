/*
 * Sonar C# Plugin :: FxCop
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
package org.sonar.plugins.csharp.fxcop;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.sonar.api.resources.Project;
import org.sonar.plugins.csharp.api.CSharpConfiguration;

public class FxCopSensorTest {

  @Test
  public void testShouldExecute() throws Exception {
    Configuration conf = new BaseConfiguration();
    FxCopSensor sensor = new FxCopSensor(null, null, null, null, null, new CSharpConfiguration(conf));

    Project project = mock(Project.class);
    when(project.getLanguageKey()).thenReturn("java");
    assertFalse(sensor.shouldExecuteOnProject(project));

    when(project.getLanguageKey()).thenReturn("cs");
    assertTrue(sensor.shouldExecuteOnProject(project));

    conf.addProperty(FxCopConstants.MODE, FxCopConstants.MODE_SKIP);
    sensor = new FxCopSensor(null, null, null, null, null, new CSharpConfiguration(conf));
    assertFalse(sensor.shouldExecuteOnProject(project));
  }

}
