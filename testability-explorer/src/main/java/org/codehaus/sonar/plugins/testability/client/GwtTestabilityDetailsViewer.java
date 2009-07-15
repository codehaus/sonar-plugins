package org.codehaus.sonar.plugins.testability.client;

import org.sonar.plugins.api.web.gwt.client.AbstractResourceTab;
import org.sonar.plugins.api.web.gwt.client.ResourcePanel;
import org.sonar.plugins.api.web.gwt.client.webservices.Resource;
import org.sonar.plugins.api.web.gwt.client.webservices.WSMetrics.Metric;

public class GwtTestabilityDetailsViewer extends AbstractResourceTab {
  
  public static final String GWT_ID = "org.codehaus.sonar.plugins.testability.client.GwtTestabilityDetailsViewer";
  
  @Override
  protected ResourcePanel getHeaderPanel(Resource resource) {
    return new TestabilityMetricsHeaderWidget(resource);
  }
  
  @Override
  protected ResourcePanel getMainPanel(Resource resource) {
    return null;
  }

  @Override
  protected void exportJavascript() {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected String getGwtId() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected boolean isDefault(Metric metric, Resource resource) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  protected boolean isForResource(Resource resource) {
    // TODO Auto-generated method stub
    return false;
  }

}
