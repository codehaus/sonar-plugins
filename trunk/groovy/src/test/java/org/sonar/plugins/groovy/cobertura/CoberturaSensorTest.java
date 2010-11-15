/*
 * Sonar Groovy Plugin
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

package org.sonar.plugins.groovy.cobertura;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.test.TestUtils;

import static org.mockito.Mockito.mock;

public class CoberturaSensorTest {

  private CoberturaSensor sensor;

  @Before
  public void setUp() throws Exception {
    sensor = new CoberturaSensor(null, null);
  }

  @Test
  public void shouldParseReport() {
    // see SONARPLUGINS-696
    SensorContext context = mock(SensorContext.class);
    sensor.parseReport(TestUtils.getResource(getClass(), "coverage.xml"), context);
  }
}
