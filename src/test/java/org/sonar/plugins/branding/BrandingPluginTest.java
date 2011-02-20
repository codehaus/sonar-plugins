/*
 * Sonar Branding Plugin
 * Copyright (C) 2011 SonarSource
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

package org.sonar.plugins.branding;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class BrandingPluginTest {

  private BrandingPlugin plugin;

  @Before
  public void setUp() {
    plugin = new BrandingPlugin();
  }

  @Test
  public void testPluginDefinition() {
    assertThat(plugin.getExtensions().size(), greaterThan(0));
  }

  /**
   * see SONAR-1898
   */
  @Test
  public void testDeprecatedMethods() {
    // Compatibility with Sonar 1.12
    assertThat(plugin.getKey(), is("branding"));
    assertThat(plugin.getName(), is("Branding"));
    assertThat(plugin.getDescription(), notNullValue());
  }

}
