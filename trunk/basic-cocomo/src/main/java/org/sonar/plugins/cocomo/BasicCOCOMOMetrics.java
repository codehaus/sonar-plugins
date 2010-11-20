/*
 * Sonar Basic-COCOMO Plugin
 * Copyright (C) 2010 Xup BV.
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

package org.sonar.plugins.cocomo;

import org.sonar.api.measures.Metrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.CoreMetrics;

import java.util.Arrays;
import java.util.List;

/**
 * {@inheritDoc}
 */
public class BasicCOCOMOMetrics implements Metrics {

  public static final Metric COCOMO_EFFORT_APPLIED = new Metric("cocomo_effort_applied", "Effort Applied", "Estimated effort applied in man months", Metric.ValueType.FLOAT, -1, false, CoreMetrics.DOMAIN_GENERAL);
  public static final Metric COCOMO_DEV_TIME = new Metric("cocomo_development_time", "Development time", "Estimated development time in months", Metric.ValueType.FLOAT, -1, false, CoreMetrics.DOMAIN_GENERAL);
  public static final Metric COCOMO_PEOPLE_REQ = new Metric("cocomo_people_required", "People", "The estimated  number of required people.", Metric.ValueType.FLOAT, -1, false, CoreMetrics.DOMAIN_GENERAL);
  public static final Metric COCOMO_COST = new Metric("cocomo_cost", "Cost", "Estimated total costs", Metric.ValueType.INT, -1, false, CoreMetrics.DOMAIN_GENERAL);

  /**
   * {@inheritDoc}
   */
  public final List<Metric> getMetrics() {
    return Arrays.asList(COCOMO_EFFORT_APPLIED, COCOMO_DEV_TIME, COCOMO_PEOPLE_REQ, COCOMO_COST);
  }
}
