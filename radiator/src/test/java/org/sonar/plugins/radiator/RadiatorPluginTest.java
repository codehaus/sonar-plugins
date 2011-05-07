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

package org.sonar.plugins.radiator;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class RadiatorPluginTest {
  private RadiatorPlugin plugin;

  @Before
  public void setUp() {
    plugin = new RadiatorPlugin();
  }

  @Test
  public void testPluginDefinition() {
    assertThat(plugin.getExtensions().size(), greaterThan(0));
  }

  /**
   * See SONAR-1898
   */
  @Test
  public void testDeprecatedMethods() {
    assertThat(plugin.getKey(), notNullValue());
    assertThat(plugin.getName(), notNullValue());
    assertThat(plugin.getDescription(), notNullValue());
  }
}
