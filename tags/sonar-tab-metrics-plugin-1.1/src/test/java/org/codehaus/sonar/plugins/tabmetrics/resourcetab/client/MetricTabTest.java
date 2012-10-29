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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * MetricTab Test
 */
public class MetricTabTest {

  @Test
  public void testMetricTab() {
    MetricTab metricTab = new MetricTab("nloc", "Code lines", "Number of code lines", 100.0);

    assertEquals(metricTab.getKey(), "nloc");
    assertEquals(metricTab.getName(), "Code lines");
    assertEquals(metricTab.getDescription(), "Number of code lines");
    assertTrue(metricTab.getValue().equals(100.0));
    assertNull(metricTab.getData());
    assertTrue(metricTab.isNumeric());

    metricTab = new MetricTab("test_data", "Test data", null, "All test has been successfull");

    assertEquals(metricTab.getKey(), "test_data");
    assertEquals(metricTab.getName(), "Test data");
    assertEquals(metricTab.getDescription(), "");
    assertNull(metricTab.getValue());
    assertEquals(metricTab.getData(), "All test has been successfull");
    assertFalse(metricTab.isNumeric());
  }
}
