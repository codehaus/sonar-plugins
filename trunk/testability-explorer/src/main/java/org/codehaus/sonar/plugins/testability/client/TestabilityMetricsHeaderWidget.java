package org.codehaus.sonar.plugins.testability.client;

import java.util.Arrays;
import java.util.List;

import org.codehaus.sonar.plugins.testability.client.webservices.WSTestabilityMetrics;
import org.sonar.plugins.api.web.gwt.client.HeaderWidget;
import org.sonar.plugins.api.web.gwt.client.webservices.Resource;
import org.sonar.plugins.api.web.gwt.client.webservices.WSMetrics;
import org.sonar.plugins.api.web.gwt.client.webservices.WSMetrics.MetricLabel;

import com.google.gwt.user.client.ui.Grid;

public class TestabilityMetricsHeaderWidget extends HeaderWidget {
  
  private static MetricLabel[] METRIC_LABELS = new MetricLabel[] {
    new WSMetrics.MetricLabel(WSTestabilityMetrics.TESTABILITY_COST, "Testability Cost")
  };
  
  public TestabilityMetricsHeaderWidget(Resource resource) {
    super(resource);
  }

  @Override
  public int getGridCols() {
    return 2;
  }

  @Override
  public int getGridRows() {
    return 1;
  }

  @Override
  public List<MetricLabel> getMetrics() {
    return Arrays.asList(METRIC_LABELS);
  }

  @Override
  public void init(Grid main) {
    main.setWidth("100%");
    addMetricToPanel(0, 0, WSTestabilityMetrics.TESTABILITY_COST);
  }

}
