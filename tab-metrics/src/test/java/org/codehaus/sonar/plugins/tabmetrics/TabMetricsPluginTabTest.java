/*
 * Sonar Tab Metrics Plugin
 * Copyright (C) 2012 eXcentia
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
package org.codehaus.sonar.plugins.tabmetrics;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Metrics Tab Test
 */
public class TabMetricsPluginTabTest {

  private static TabMetricsPluginTab tab;

  @BeforeClass
  public static void setUp() {
    tab = new TabMetricsPluginTab();
  }

  @Test
  public void testWidgetsId() {
    assertEquals(tab.getId(), "metrics");
  }

  @Test
  public void testWidgetsTitle() {
    assertEquals(tab.getTitle(), "Metrics");
  }

  @Test
  public void testWidgetsPath() {
    assertEquals(tab.getTemplatePath(), "/TabMetrics.html.erb");
  }
}