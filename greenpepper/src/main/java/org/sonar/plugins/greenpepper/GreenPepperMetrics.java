/*
 * Sonar GreenPepper Plugin
 * Copyright (C) 2009 SonarSource
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

package org.sonar.plugins.greenpepper;

import com.google.common.collect.ImmutableList;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.List;

public class GreenPepperMetrics implements Metrics {

  public static final Metric TESTS = new Metric.Builder("greenpepper_tests", "GreenPepper tests", Metric.ValueType.INT)
    .setDescription("Number of GreenPepper tests")
    .setDirection(-1)
    .setQualitative(false)
    .setDomain(CoreMetrics.DOMAIN_TESTS)
    .create();

  public static final Metric TEST_FAILURES = new Metric.Builder("greenpepper_test_failures", "GreenPepper test failures", Metric.ValueType.INT)
    .setDescription("Number of GreenPepper test failures")
    .setDirection(-1)
    .setQualitative(false)
    .setDomain(CoreMetrics.DOMAIN_TESTS)
    .create();

  public static final Metric TEST_ERRORS = new Metric.Builder("greenpepper_test_errors", "GreenPepper test errors", Metric.ValueType.INT)
    .setDescription("Number of GreenPepper test errors")
    .setDirection(-1)
    .setQualitative(false)
    .setDomain(CoreMetrics.DOMAIN_TESTS)
    .create();

  public static final Metric SKIPPED_TESTS = new Metric.Builder("greenpepper_skipped_tests", "GreenPepper skipped tests", Metric.ValueType.INT)
    .setDescription("Number of skipped GreenPepper tests")
    .setDirection(-1)
    .setQualitative(false)
    .setDomain(CoreMetrics.DOMAIN_TESTS)
    .create();

  public static final Metric TEST_SUCCESS_DENSITY = new Metric.Builder("greenpepper_test_success_density", "GreenPepper test success (%)", Metric.ValueType.PERCENT)
    .setDescription("Ratio of successful GreenPepper tests")
    .setDirection(1)
    .setQualitative(true)
    .setDomain(CoreMetrics.DOMAIN_TESTS)
    .create();

  public List<Metric> getMetrics() {
    return ImmutableList.of(TESTS, TEST_ERRORS, TEST_FAILURES, SKIPPED_TESTS, TEST_SUCCESS_DENSITY);
  }
}
