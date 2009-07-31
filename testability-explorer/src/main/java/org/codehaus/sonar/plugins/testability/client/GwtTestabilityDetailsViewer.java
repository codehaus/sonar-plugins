package org.codehaus.sonar.plugins.testability.client;

import org.codehaus.sonar.plugins.testability.client.webservices.WSTestabilityMetrics;
import org.sonar.plugins.api.web.gwt.client.AbstractSourcesResourceTab;
import org.sonar.plugins.api.web.gwt.client.ResourcePanel;
import org.sonar.plugins.api.web.gwt.client.SourceDecorator;
import org.sonar.plugins.api.web.gwt.client.webservices.Resource;
import org.sonar.plugins.api.web.gwt.client.webservices.WSMetrics.Metric;

public class GwtTestabilityDetailsViewer extends AbstractSourcesResourceTab {
  
  public static final String GWT_ID = "org.codehaus.sonar.plugins.testability.GwtTestabilityDetailsViewer";
  
  @Override
  protected ResourcePanel getHeaderPanel(Resource resource) {
    return new TestabilityMetricsHeaderWidget(resource);
  }

  @Override
  protected void exportJavascript() {
    exportNativeJavascript(this);
  }

  public static native void exportNativeJavascript(GwtTestabilityDetailsViewer obj) /*-{
   $wnd.load_org_codehaus_sonar_plugins_testability_GwtTestabilityDetailsViewer = function() {
      obj.@org.codehaus.sonar.plugins.testability.client.GwtTestabilityDetailsViewer::loadContainer()();
    }
    $wnd.on_resource_loaded_org_codehaus_sonar_plugins_testability_GwtTestabilityDetailsViewer = function() {
      obj.@org.codehaus.sonar.plugins.testability.client.GwtTestabilityDetailsViewer::onResourceLoaded()();
    }
  }-*/;

  @Override
  protected String getGwtId() {
    return GWT_ID;
  }

  @Override
  protected boolean isDefault(Metric metric, Resource resource) {
    return WSTestabilityMetrics.METHOD_DETAILS_COST.equals(metric) || WSTestabilityMetrics.TESTABILITY_COST.equals(metric);
  }

  @Override
  protected boolean isForResource(Resource resource) {
    return resource.getScope().equals(Resource.SCOPE_FILE) && resource.getQualifier().equals(Resource.QUALIFIER_CLASS);
  }

  @Override
  protected SourceDecorator<?> getDecorator(Resource resource) {
    return new TestabilitySourceDecorator(resource);
  }

}
