package org.codehaus.sonar.plugins.testability.client;

import java.util.Arrays;

import org.codehaus.sonar.plugins.testability.client.webservices.WSTestabilityMetrics;
import org.sonar.api.web.gwt.client.webservices.Measure;
import org.sonar.api.web.gwt.client.webservices.Resource;
import org.sonar.api.web.gwt.client.widgets.AbstractViewerHeader;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class GwtTestabilitySourceHeader extends AbstractViewerHeader {

  public GwtTestabilitySourceHeader(Resource resource) {
    super(resource, Arrays.asList(WSTestabilityMetrics.TESTABILITY_COST));
  }

  @Override
  protected void display(FlowPanel header, Resource resource) {
    HorizontalPanel panel = new HorizontalPanel();
    header.add(panel);
    Measure measure = resource.getMeasure(WSTestabilityMetrics.TESTABILITY_COST);
    if (measure == null) {
      addCell(panel, "Cost", "No data available");
    } else {
      addBigCell(panel, "Cost: " + measure.getFormattedValue());
    }
  }

}
