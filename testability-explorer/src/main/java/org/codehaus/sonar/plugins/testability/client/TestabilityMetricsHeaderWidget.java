package org.codehaus.sonar.plugins.testability.client;

import java.util.Arrays;
import java.util.List;

import org.codehaus.sonar.plugins.testability.client.webservices.WSTestabilityMetrics;
import org.sonar.plugins.api.web.gwt.client.HeaderWidget;
import org.sonar.plugins.api.web.gwt.client.webservices.Measure;
import org.sonar.plugins.api.web.gwt.client.webservices.QueryCallBack;
import org.sonar.plugins.api.web.gwt.client.webservices.Resource;
import org.sonar.plugins.api.web.gwt.client.webservices.Resources;
import org.sonar.plugins.api.web.gwt.client.webservices.ResourcesQuery;
import org.sonar.plugins.api.web.gwt.client.webservices.WSMetrics;
import org.sonar.plugins.api.web.gwt.client.webservices.WSMetrics.MetricLabel;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

public class TestabilityMetricsHeaderWidget extends HeaderWidget {

  private final class HeaderQueryCallBack implements QueryCallBack<Resources> {
    private static final String ERROR_RETRIEVING_TESTABILITY_COST = "Error retrieving testability cost";
    private Grid grid;

    public HeaderQueryCallBack(Grid main) {
      this.grid = main;
    }

    public void onError(int errorCode, String errorMessage) {
      getGrid().setWidget(0, 0, new Label(ERROR_RETRIEVING_TESTABILITY_COST));
    }

    public void onResponse(Resources response, JavaScriptObject jsonRawResponse) {
      Resource resource = response.getResources().get(0);
      Measure measure = resource.getMeasures().get(WSTestabilityMetrics.TESTABILITY_COST);
      getGrid().setWidget(0, 0, new Label("Testability Cost: "));
      getGrid().setWidget(0, 1, new Label(measure.getFormattedValue()));
    }

    public void onTimeout() {
      getGrid().setWidget(0, 0, new Label(ERROR_RETRIEVING_TESTABILITY_COST));
    }

    public Grid getGrid() {
      return this.grid;
    }

    public void setGrid(Grid grid) {
      this.grid = grid;
    }
  }

  private static final MetricLabel[] METRIC_LABELS = new MetricLabel[] { new MetricLabel(WSTestabilityMetrics.TESTABILITY_COST,
      "Testability Cost") };

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

  public void init(Grid main) {
    main.setWidth("100%");
    // I have to make a query to get the testability cost measure
    ResourcesQuery.get(getResource().getKey()).setMetrics(WSMetrics.getDefaultMetrics()).execute(new HeaderQueryCallBack(main));
  }

}
