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

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class TechnicalDebtDecoratorTest {

  private TechnicalDebtDecorator decorator;

  @Before
  public void setUp() {
    decorator = new TechnicalDebtDecorator(new Settings(), new Project("project"));
  }

  @Test
  public void dependsOnMetrics() {
    assertThat(decorator.dependsOnMetrics().size(), greaterThan(0));
  }

  @Test
  public void generatesMetrics() {
    assertThat(decorator.generatesMetrics().size(), is(4));
  }

  @Test
  public void shouldExecuteOnAnyProject() {
    assertThat(decorator.shouldExecuteOnProject(mock(Project.class)), is(true));
  }
}
