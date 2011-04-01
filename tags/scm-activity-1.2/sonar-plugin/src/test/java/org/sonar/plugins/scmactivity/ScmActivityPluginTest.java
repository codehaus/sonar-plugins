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
import org.junit.Test;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Evgeny Mandrikov
 */
public class ScmActivityPluginTest {
  private ScmActivityPlugin plugin;

  @Before
  public void setUp() throws Exception {
    plugin = new ScmActivityPlugin();
  }

  @Test
  public void testGetExtensions() throws Exception {
    assertThat(plugin.getExtensions().size(), greaterThan(0));
  }

  /**
   * see SONAR-1898
   */
  @Test
  public void testDeprecatedMethods() {
    assertThat(plugin.getKey(), notNullValue());
    assertThat(plugin.getName(), notNullValue());
    assertThat(plugin.getDescription(), notNullValue());
  }

}
