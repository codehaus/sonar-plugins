package org.sonar.plugins.qi;

import org.sonar.api.measures.Metric;
import org.sonar.api.measures.CoreMetrics;

import java.util.List;
import java.util.Arrays;

/**
 * The metrics definition for the plugin
 */
public class QIMetrics implements org.sonar.api.measures.Metrics {

  /**
   * The QI metric
   */
  public static final Metric QI_QUALITY_INDEX = new Metric("qi-quality-index", "Quality Index",
    "The quality index of a project", Metric.ValueType.FLOAT, 1, true, CoreMetrics.DOMAIN_GENERAL);

  /**
   * The coding axis metric for QI
   */
  public static final Metric QI_CODING_VIOLATIONS = new Metric("qi-coding-violations", "Coding Violations",
    "Coding Violations", Metric.ValueType.FLOAT, -1, true, CoreMetrics.DOMAIN_RULES);

  /**
   * A technical metric to propagate weighted coding violations bottom up
   */
  public static final Metric QI_CODING_WEIGHTED_VIOLATIONS = new Metric("qi-coding-weighted-violations", "Coding Weighted Violations",
    "Coding Weighted Violations", Metric.ValueType.INT, 0, false, CoreMetrics.DOMAIN_RULES);

  /**
   * The style axis metric for QI
   */
  public static final Metric QI_STYLE_VIOLATIONS = new Metric("qi-style-violations", "Style Violations",
    "Style Violations", Metric.ValueType.FLOAT, -1, true, CoreMetrics.DOMAIN_RULES);

  /**
   * A technical metric to propagate weighted style violations bottom up
   */
  public static final Metric QI_STYLE_WEIGHTED_VIOLATIONS = new Metric("qi-style-weighted-violations", "Style Weighted Violations",
    "Style Weighted Violations", Metric.ValueType.INT, 0, false, CoreMetrics.DOMAIN_RULES);

  /**
   * The complexity axis metric for QI
   */
  public static final Metric QI_COMPLEXITY = new Metric("qi-complexity", "Complexity",
    "Complexityt", Metric.ValueType.FLOAT, -1, true, CoreMetrics.DOMAIN_COMPLEXITY);

  /**
   * The coverage axis metric for QI
   */
  public static final Metric QI_TEST_COVERAGE = new Metric("qi-test-coverage", "Test Coverage",
    "Test Coverage", Metric.ValueType.FLOAT, -1, true, CoreMetrics.DOMAIN_TESTS);

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
    "Complexity distribution", Metric.ValueType.DATA, 0, false, CoreMetrics.DOMAIN_COMPLEXITY);

  /**
   * @return the declare metrics
   */
  public List<Metric> getMetrics() {
    return Arrays.asList(QI_QUALITY_INDEX, QI_CODING_VIOLATIONS, QI_CODING_WEIGHTED_VIOLATIONS,
      QI_STYLE_VIOLATIONS, QI_STYLE_WEIGHTED_VIOLATIONS, QI_TEST_COVERAGE,
      QI_COMPLEXITY, QI_COMPLEXITY_FACTOR, QI_COMPLEXITY_FACTOR_METHODS, QI_COMPLEX_DISTRIBUTION);
  }
}
