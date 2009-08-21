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

package org.sonar.plugins.greenpepper;

import org.sonar.api.measures.Metrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.CoreMetrics;

import java.util.List;
import java.util.ArrayList;

public class GreenPepperMetrics implements Metrics {

  public static final Metric GREENPEPPER_TESTS = new Metric("greenpepper_tests", "GreenPepper tests","Number of GreenPepper tests", Metric.ValueType.INT, -1, false, CoreMetrics.DOMAIN_TESTS);
  public static final Metric GREENPEPPER_TEST_FAILURES = new Metric("greenpepper_test_failures","GreenPepper test failures", "Number of GreenPepper test failures", Metric.ValueType.INT, -1, false, CoreMetrics.DOMAIN_TESTS);
  public static final Metric GREENPEPPER_TEST_ERRORS = new Metric("greenpepper_test_errors","GreenPepper test errors", "Number of GreenPepper test errors", Metric.ValueType.INT, -1, false,CoreMetrics.DOMAIN_TESTS);
  public static final Metric GREENPEPPER_SKIPPED_TESTS = new Metric("greenpepper_skipped_tests","GreenPepper skipped tests", "Number of skipped GreenPepper tests", Metric.ValueType.INT, -1, false,CoreMetrics.DOMAIN_TESTS);
  public static final Metric GREENPEPPER_TEST_SUCCESS_DENSITY = new Metric("greenpepper_test_success_density","GreenPepper test success (%)", "Ratio of successful GreenPepper tests", Metric.ValueType.PERCENT, 1, true,CoreMetrics.DOMAIN_TESTS);

  public List<Metric> getMetrics() {
    ArrayList<Metric> metrics = new ArrayList<Metric>();
    metrics.add(GREENPEPPER_TESTS);
    metrics.add(GREENPEPPER_TEST_ERRORS);
    metrics.add(GREENPEPPER_TEST_FAILURES);
    metrics.add(GREENPEPPER_SKIPPED_TESTS);
    metrics.add(GREENPEPPER_TEST_SUCCESS_DENSITY);
    return metrics;
  }
}
