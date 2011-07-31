/*
 * Sonar Scala Plugin
 * Copyright (C) 2011 Felix Müller
 * felix.mueller.berlin@googlemail.com
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
package org.sonar.plugins.scala;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;
import org.sonar.plugins.scala.language.Scala;

public class ScalaSensorTest {

  private ScalaSensor scalaSensor;

  @Before
  public void setUp() {
    scalaSensor = new ScalaSensor(Scala.INSTANCE);
  }

  @Test
  public void shouldOnlyExecuteOnScalaProjects() {
    Project scalaProject = mock(Project.class);
    when(scalaProject.getLanguage()).thenReturn(Scala.INSTANCE);
    Project javaProject = mock(Project.class);
    when(javaProject.getLanguage()).thenReturn(Java.INSTANCE);

    assertThat(scalaSensor.shouldExecuteOnProject(scalaProject), is(true));
    assertThat(scalaSensor.shouldExecuteOnProject(javaProject), is(false));
  }
}