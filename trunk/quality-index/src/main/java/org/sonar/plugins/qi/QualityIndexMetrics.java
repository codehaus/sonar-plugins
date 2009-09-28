package org.sonar.plugins.qi;

import org.sonar.api.measures.Metrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.CoreMetrics;

import java.util.List;
import java.util.Arrays;

public class QualityIndexMetrics implements Metrics {

  public static final Metric QI_QUALITY_INDEX = new Metric("qi-quality-index", "Quality Index",
    "The quality index of a project", Metric.ValueType.INT, 1, true, CoreMetrics.DOMAIN_GENERAL);

  public static final Metric QI_CODING_VIOLATIONS = new Metric("qi-coding-violations", "Coding Violations",
    "Coding Violations", Metric.ValueType.INT, -1, true, CoreMetrics.DOMAIN_RULES);

  public static final Metric QI_STYLE_VIOLATIONS = new Metric("qi-style-violations", "Style Violations",
    "Style Violations", Metric.ValueType.INT, -1, true, CoreMetrics.DOMAIN_RULES);

  public static final Metric QI_COMPLEXITY = new Metric("qi-complexity", "Complexity",
    "Complexityt", Metric.ValueType.INT, -1, true, CoreMetrics.DOMAIN_COMPLEXITY);

  public static final Metric QI_TEST_COVERAGE = new Metric("qi-test-coverage", "Test Coverage",
    "Test Coverage", Metric.ValueType.INT, -1, true, CoreMetrics.DOMAIN_TESTS);

  public List<Metric> getMetrics() {
    return Arrays.asList(QI_QUALITY_INDEX, QI_CODING_VIOLATIONS, QI_COMPLEXITY, QI_STYLE_VIOLATIONS, QI_TEST_COVERAGE);
  }
}
