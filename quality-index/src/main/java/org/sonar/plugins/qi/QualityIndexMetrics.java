package org.sonar.plugins.qi;

import org.sonar.api.measures.Metrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.CoreMetrics;

import java.util.List;
import java.util.Arrays;

public class QualityIndexMetrics implements Metrics {

  public static final Metric QUALITY_INDEX = new Metric("quality-index", "Quality Index",
    "The metric reflects on the quality index of a project", Metric.ValueType.INT, 1, true,
    CoreMetrics.DOMAIN_GENERAL);

  public List<Metric> getMetrics() {
    return Arrays.asList(QUALITY_INDEX);
  }
}
