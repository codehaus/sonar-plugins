package org.codehaus.sonar.plugins.testability.client;

import java.util.List;

import org.sonar.plugins.api.web.gwt.client.HeaderWidget;
import org.sonar.plugins.api.web.gwt.client.webservices.Resource;
import org.sonar.plugins.api.web.gwt.client.webservices.WSMetrics.MetricLabel;

import com.google.gwt.user.client.ui.Grid;

public class TestabilityMetricsHeaderWidget extends HeaderWidget {
  public TestabilityMetricsHeaderWidget(Resource resource) {
    super(resource);
  }

  @Override
  public int getGridCols() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getGridRows() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public List<MetricLabel> getMetrics() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void init(Grid main) {
    // TODO Auto-generated method stub
  }

}
