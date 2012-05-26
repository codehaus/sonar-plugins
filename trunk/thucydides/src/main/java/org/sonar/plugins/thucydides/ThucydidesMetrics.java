/*
 * Sonar Thucydides Plugin
 * Copyright (C) 2012 OTS SA
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

package org.sonar.plugins.thucydides;

import java.util.Arrays;
import java.util.List;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

public final class ThucydidesMetrics implements Metrics {

  private static final String THUCYDIDES_DOMAIN = "Thucydides";

  public static final Metric THUCYDIDES_TESTS = new Metric.Builder("thucydides-tests", "Thucydides tests", Metric.ValueType.INT)
      .setDescription("Thucydides tests")
      .setQualitative(false)
      .setDomain(THUCYDIDES_DOMAIN)
      .create();

  public static final Metric THUCYDIDES_TESTS_PASSED = new Metric.Builder("thucydides-tests-passed", "Thucydides passed tests", Metric.ValueType.INT)
      .setDescription("Thucydides passed tests")
      .setQualitative(false)
      .setDomain(THUCYDIDES_DOMAIN)
      .create();

  public static final Metric THUCYDIDES_TESTS_PENDING = new Metric.Builder("thucydides-tests-pending", "Thucydides pending tests", Metric.ValueType.INT)
      .setDescription("Thucydides pending tests")
      .setQualitative(false)
      .setDomain(THUCYDIDES_DOMAIN)
      .create();

  public static final Metric THUCYDIDES_TESTS_FAILED = new Metric.Builder("thucydides-tests-failed", "Thucydides failed tests", Metric.ValueType.INT)
      .setDescription("Thucydides failed tests")
      .setQualitative(false)
      .setDomain(THUCYDIDES_DOMAIN)
      .create();

  public static final Metric THUCYDIDES_SUCCESS_DENSITY = new Metric.Builder("thucydides-success-density", "Thucydides success density", Metric.ValueType.PERCENT)
      .setDescription("Thucydides Success Density")
      .setQualitative(false)
      .setWorstValue(0.0)
      .setBestValue(100.0)
      .setOptimizedBestValue(true)
      .setDomain(THUCYDIDES_DOMAIN)
      .create();

  public static final Metric THUCYDIDES_TESTS_DURATION = new Metric.Builder("thucydides-tests-duration", "Thucydides tests duration", Metric.ValueType.MILLISEC)
      .setDescription("Thucydides Tests Duration")
      .setQualitative(false)
      .setDomain(THUCYDIDES_DOMAIN)
      .create();

  public static final Metric THUCYDIDES_FEATURES = new Metric.Builder("thucydides-features", "Thucydides features", Metric.ValueType.INT)
      .setDescription("Thucydides tested features")
      .setQualitative(false)
      .setDomain(THUCYDIDES_DOMAIN)
      .create();

  public static final Metric THUCYDIDES_STORIES = new Metric.Builder("thucydides-stories", "Thucydides stories", Metric.ValueType.INT)
      .setDescription("Thucydides tested user stories")
      .setQualitative(false)
      .setDomain(THUCYDIDES_DOMAIN)
      .create();


  public List<Metric> getMetrics() {
    return Arrays.asList(THUCYDIDES_TESTS, 
            THUCYDIDES_TESTS_PASSED, 
            THUCYDIDES_TESTS_PENDING, 
            THUCYDIDES_TESTS_FAILED,
            THUCYDIDES_TESTS_DURATION,
            THUCYDIDES_SUCCESS_DENSITY,
            THUCYDIDES_FEATURES,
            THUCYDIDES_STORIES);
  }

}
