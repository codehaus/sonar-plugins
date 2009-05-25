package org.sonar;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.sonar.commons.Metric;
import org.sonar.plugins.api.metrics.CoreMetrics;
import org.sonar.plugins.api.metrics.Metrics;

/**
 * As the Metrics class implements org.sonar.plugins.api.Extension interface, 
 * MyOwnMetrics is a Sonar extension which allows to declare as many new Metrics as you want.
 */
public class HelloMetrics implements Metrics {

  public static final Metric MESSAGE = new Metric("message_key", "Message",
      "This is a metric to store a well known message", Metric.ValueType.STRING, -1, false,
      CoreMetrics.DOMAIN_GENERAL, false);

  // getMetrics() method is defined in the Metrics interface and is used by
  // Sonar to retrieve the list of new Metric
  public List<Metric> getMetrics() {
    return Arrays.asList(MESSAGE);

  }
}
