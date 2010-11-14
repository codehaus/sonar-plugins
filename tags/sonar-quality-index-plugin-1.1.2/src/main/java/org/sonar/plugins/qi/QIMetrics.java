/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
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

package org.sonar.plugins.qi;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;

import java.util.Arrays;
import java.util.List;

/**
 * The metrics definition for the plugin
 */
public class QIMetrics implements org.sonar.api.measures.Metrics {

  /**
   * The QI metric
   */
  public static final Metric QI_QUALITY_INDEX = new Metric("qi-quality-index", "Quality Index",
      "The quality index of a project", Metric.ValueType.FLOAT, 1, true, CoreMetrics.DOMAIN_GENERAL)
    .setBestValue(10.0).setWorstValue(0.0);

  /**
   * The coding axis metric for QI
   */
  public static final Metric QI_CODING_VIOLATIONS = new Metric("qi-coding-violations", "QI Coding Violations",
      "QI Coding Violations", Metric.ValueType.FLOAT, -1, true, CoreMetrics.DOMAIN_RULES);

  /**
   * A technical metric to propagate weighted coding violations bottom up
   */
  public static final Metric QI_CODING_WEIGHTED_VIOLATIONS = new Metric("qi-coding-weighted-violations", "QI Coding Weighted Violations",
      "QI Coding Weighted Violations", Metric.ValueType.INT, 0, false, CoreMetrics.DOMAIN_RULES);

  /**
   * The style axis metric for QI
   */
  public static final Metric QI_STYLE_VIOLATIONS = new Metric("qi-style-violations", "QI Style Violations",
      "QI Style Violations", Metric.ValueType.FLOAT, -1, true, CoreMetrics.DOMAIN_RULES);

  /**
   * A technical metric to propagate weighted style violations bottom up
   */
  public static final Metric QI_STYLE_WEIGHTED_VIOLATIONS = new Metric("qi-style-weighted-violations", "QI Style Weighted Violations",
      "QI Style Weighted Violations", Metric.ValueType.INT, 0, false, CoreMetrics.DOMAIN_RULES);

  /**
   * The complexity axis metric for QI
   */
  public static final Metric QI_COMPLEXITY = new Metric("qi-complexity", "QI Complexity",
      "QI Complexity", Metric.ValueType.FLOAT, -1, true, CoreMetrics.DOMAIN_COMPLEXITY);

  /**
   * The coverage axis metric for QI
   */
  public static final Metric QI_TEST_COVERAGE = new Metric("qi-test-coverage", "QI Test Coverage",
      "QI Test Coverage", Metric.ValueType.FLOAT, -1, true, CoreMetrics.DOMAIN_TESTS);

  /**
   * The Complexity factor metric
   */
  public static final Metric QI_COMPLEXITY_FACTOR = new Metric("qi-complexity-factor", "Complexity Factor",
      "Complexity Factor", Metric.ValueType.PERCENT, -1, true, CoreMetrics.DOMAIN_COMPLEXITY);

  /**
   * The complex methods metric
   */
  public static final Metric QI_COMPLEXITY_FACTOR_METHODS = new Metric("qi-complexity-factor-methods", "Complexity Factor Methods",
      "Complexity Factor Methods", Metric.ValueType.INT, -1, false, CoreMetrics.DOMAIN_COMPLEXITY);

  /**
   * A technical metric to propagate complexity distribution bottom up
   */
  public static final Metric QI_COMPLEX_DISTRIBUTION = new Metric("qi-complex-distrib", "Complexity distribution",
      "Complexity distribution", Metric.ValueType.DISTRIB, 0, false, CoreMetrics.DOMAIN_COMPLEXITY);

  /**
   * @return the declare metrics
   */
  public List<Metric> getMetrics() {
    return Arrays.asList(QI_QUALITY_INDEX, QI_CODING_VIOLATIONS, QI_CODING_WEIGHTED_VIOLATIONS,
        QI_STYLE_VIOLATIONS, QI_STYLE_WEIGHTED_VIOLATIONS, QI_TEST_COVERAGE,
        QI_COMPLEXITY, QI_COMPLEXITY_FACTOR, QI_COMPLEXITY_FACTOR_METHODS, QI_COMPLEX_DISTRIBUTION);
  }
}
