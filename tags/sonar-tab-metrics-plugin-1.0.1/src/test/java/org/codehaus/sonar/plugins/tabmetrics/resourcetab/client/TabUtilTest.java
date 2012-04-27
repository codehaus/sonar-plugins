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
package org.codehaus.sonar.plugins.tabmetrics.resourcetab.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tab Util Test
 */
public class TabUtilTest {

  @Test
  public void testCreateCell() {
    MetricTab metricTab = new MetricTab("nloc", "Code lines", "Number of code lines", 230.0);

    String htmlCell = TabUtil.createCell(metricTab);

    assertEquals(htmlCell, "Code lines: <b>230.0</b>");
  }
  
  @Test
  public void testGetInstance() {
    assertEquals(TabUtil.getInstance().getClass(), TabUtil.class);
  }
}
