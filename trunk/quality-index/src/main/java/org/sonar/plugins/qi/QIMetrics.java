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
  public static final Metric QI_QUALITY_INDEX = new Metric.Builder("qi-quality-index", "Quality Index", Metric.ValueType.FLOAT)
      .setDescription("The quality index of a project")
      .setDirection(Metric.DIRECTION_BETTER)
      .setQualitative(true)
      .setDomain(CoreMetrics.DOMAIN_GENERAL)
      .setBestValue(10.0)
      .setWorstValue(0.0)
      .create();

  /**
   * The coding axis metric for QI
   */
  public static final Metric QI_CODING_VIOLATIONS = new Metric.Builder("qi-coding-violations", "QI Coding Violations", Metric.ValueType.FLOAT)
      .setDescription("QI Coding Violations")
      .setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true)
      .setDomain(CoreMetrics.DOMAIN_RULES)
      .create();

  /**
   * A technical metric to propagate weighted coding violations bottom up
   */
  public static final Metric QI_CODING_WEIGHTED_VIOLATIONS = new Metric.Builder("qi-coding-weighted-violations", "QI Coding Weighted Violations", Metric.ValueType.INT)
      .setDescription("QI Coding Weighted Violations")
      .setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false)
      .setDomain(CoreMetrics.DOMAIN_RULES)
      .create();

  /**
   * The style axis metric for QI
   */
  public static final Metric QI_STYLE_VIOLATIONS = new Metric.Builder("qi-style-violations", "QI Style Violations", Metric.ValueType.FLOAT)
      .setDescription("QI Style Violations")
      .setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true)
      .setDomain(CoreMetrics.DOMAIN_RULES)
      .create();

  /**
   * A technical metric to propagate weighted style violations bottom up
   */
  public static final Metric QI_STYLE_WEIGHTED_VIOLATIONS = new Metric.Builder("qi-style-weighted-violations", "QI Style Weighted Violations", Metric.ValueType.INT)
      .setDescription("QI Style Weighted Violations")
      .setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false)
      .setDomain(CoreMetrics.DOMAIN_RULES)
      .create();

  /**
   * The complexity axis metric for QI
   */
  public static final Metric QI_COMPLEXITY = new Metric.Builder("qi-complexity", "QI Complexity", Metric.ValueType.FLOAT)
      .setDescription("QI Complexity")
      .setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true)
      .setDomain(CoreMetrics.DOMAIN_COMPLEXITY)
      .create();

  /**
   * The coverage axis metric for QI
   */
  public static final Metric QI_TEST_COVERAGE = new Metric.Builder("qi-test-coverage", "QI Test Coverage", Metric.ValueType.FLOAT)
      .setDescription("QI Test Coverage")
      .setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true)
      .setDomain(CoreMetrics.DOMAIN_TESTS)
      .create();

  /**
   * The Complexity factor metric
   */
  public static final Metric QI_COMPLEXITY_FACTOR = new Metric.Builder("qi-complexity-factor", "Complexity Factor", Metric.ValueType.PERCENT)
      .setDescription("Complexity Factor")
      .setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true)
      .setDomain(CoreMetrics.DOMAIN_COMPLEXITY)
      .create();

  /**
   * The complex methods metric
   */
  public static final Metric QI_COMPLEXITY_FACTOR_METHODS = new Metric.Builder("qi-complexity-factor-methods", "Complexity Factor Methods", Metric.ValueType.INT)
      .setDescription("Complexity Factor Methods")
      .setDirection(Metric.DIRECTION_WORST)
      .setQualitative(false)
      .setDomain(CoreMetrics.DOMAIN_COMPLEXITY)
      .create();

  /**
   * A technical metric to propagate complexity distribution bottom up
   */
  public static final Metric QI_COMPLEX_DISTRIBUTION = new Metric.Builder("qi-complex-distrib", "Complexity distribution", Metric.ValueType.DISTRIB)
      .setDescription("Complexity distribution")
      .setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false)
      .setDomain(CoreMetrics.DOMAIN_COMPLEXITY)
      .create();

  /**
   * @return the declare metrics
   */
  public List<Metric> getMetrics() {
    return Arrays.asList(QI_QUALITY_INDEX, QI_CODING_VIOLATIONS, QI_CODING_WEIGHTED_VIOLATIONS,
        QI_STYLE_VIOLATIONS, QI_STYLE_WEIGHTED_VIOLATIONS, QI_TEST_COVERAGE,
        QI_COMPLEXITY, QI_COMPLEXITY_FACTOR, QI_COMPLEXITY_FACTOR_METHODS, QI_COMPLEX_DISTRIBUTION);
  }
}
