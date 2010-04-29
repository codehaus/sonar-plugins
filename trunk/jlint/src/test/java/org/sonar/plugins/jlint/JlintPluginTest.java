/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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

package org.sonar.plugins.jlint;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Evgeny Mandrikov
 */
public class JlintPluginTest {
  private JlintPlugin plugin;

  @Before
  public void setUp() throws Exception {
    plugin = new JlintPlugin();
  }

  @Test
  public void testPlugin() throws Exception {
    assertThat(plugin.getKey(), notNullValue());
    assertThat(plugin.getName(), notNullValue());
    assertThat(plugin.getDescription(), notNullValue());
    assertThat(plugin.getExtensions(), notNullValue());
    assertThat(plugin.getExtensions().size(), is(3));
  }
}
