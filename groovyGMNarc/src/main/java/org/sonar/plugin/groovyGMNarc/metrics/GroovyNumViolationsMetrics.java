/**
 * Sonar, open source software quality management tool.
 * Copyright (C) ${year} ${name}
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
 *
 */

package org.sonar.plugin.groovyGMNarc.metrics;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.Arrays;
import java.util.List;

public class GroovyNumViolationsMetrics implements Metrics {

  public static final Metric VIOLATION = new Metric("violations_key", "Violations",
    "Number of Violations", Metric.ValueType.INT, -1, false,
    CoreMetrics.DOMAIN_GENERAL);


  public static final Metric RANDOM = new Metric("random", "Random",
    "Random value", Metric.ValueType.FLOAT, Metric.DIRECTION_BETTER, false,
    CoreMetrics.DOMAIN_GENERAL);

  // getMetrics() method is defined in the Metrics interface and is used by
  // Sonar to retrieve the list of new Metric
  public List<Metric> getMetrics() {
    return Arrays.asList(VIOLATION);
  }
}