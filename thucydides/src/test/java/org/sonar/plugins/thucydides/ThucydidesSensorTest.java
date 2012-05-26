/*
 * Sonar Thucydides Plugin
 * Copyright (C) 2012 OTS SA
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
package org.sonar.plugins.thucydides;

import static org.junit.Assert.*;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Java;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class ThucydidesSensorTest {

  private final Project project = new Project("my project");

  @Before
  public void setUp() {
    
  }

  @Test
  public void shouldExecuteOnProjectReturnsTrue() {
    project.setLanguageKey(Java.KEY);
    ThucydidesSensor sensor = new ThucydidesSensor(null);
    assertTrue(sensor.shouldExecuteOnProject(project));
  }

  @Test
  public void shouldExecuteOnProjectReturnsFalse() {
    ThucydidesSensor sensor = new ThucydidesSensor(null);
    assertFalse(sensor.shouldExecuteOnProject(project));
  }
}
