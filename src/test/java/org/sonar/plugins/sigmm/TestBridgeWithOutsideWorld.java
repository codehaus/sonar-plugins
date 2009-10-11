/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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
package org.sonar.plugins.sigmm;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TestBridgeWithOutsideWorld {

  @Test
  public void testPluginsMetrics() {
    assertThat(new MMMetrics().getMetrics().size(), is(7));
  }

  @Test
  public void testDefinedExtensions() {
    assertThat(new MMPlugin().getExtensions().size(), equalTo(5));
  }

  @Test
  public void testDecoratorDependsUpon() {
    assertThat(new MMDecorator().dependsUpon().size(), equalTo(5));
  }

  @Test
  public void testDecoratorDependedUpon() {
    assertThat(new MMDecorator().dependedUpon().size(), equalTo(5));
  }

  @Test
  public void testDistributionDecoratorDependedUpon() {
    assertThat(new MMDistributionDecorator().dependedUpon().size(), equalTo(2));
  }

  @Test
  public void testSensorDependsUpon() {
    assertThat(new MMSensor(null).dependsUpon().size(), equalTo(1));
  }
}
