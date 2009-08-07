package org.codehaus.sonar.plugins.testability.client.webservices;

import org.sonar.plugins.api.web.gwt.client.webservices.WSMetrics.Metric;

public final class WSTestabilityMetrics {
  public static final Metric TESTABILITY_COST = new Metric("testability_cost", true);
  public static final Metric METHOD_DETAILS_COST = new Metric("testability_method_details", true);

  private WSTestabilityMetrics() {
    // Constant Class
  }
}
