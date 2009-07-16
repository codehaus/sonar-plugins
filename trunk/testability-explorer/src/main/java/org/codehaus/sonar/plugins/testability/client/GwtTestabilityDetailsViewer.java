package org.codehaus.sonar.plugins.testability.client;

import org.sonar.plugins.api.web.gwt.client.AbstractSourcesResourceTab;
import org.sonar.plugins.api.web.gwt.client.ResourcePanel;
import org.sonar.plugins.api.web.gwt.client.SourceDecorator;
import org.sonar.plugins.api.web.gwt.client.webservices.Resource;
import org.sonar.plugins.api.web.gwt.client.webservices.WSMetrics.Metric;

public class GwtTestabilityDetailsViewer extends AbstractSourcesResourceTab {
  
  public static final String GWT_ID = "org.codehaus.sonar.plugins.testability.client.GwtTestabilityDetailsViewer";
  
  @Override
  protected ResourcePanel getHeaderPanel(Resource resource) {
    return new TestabilityMetricsHeaderWidget(resource);
  }

  @Override
  protected void exportJavascript() {
    exportNativeJavascript(this);
  }

  public static native void exportNativeJavascript(GwtTestabilityDetailsViewer gwtTestabilityDetailsViewer) /*-{
   $wnd.load_org_codehaus_sonar_plugins_testability_client_GwtTestabilityDetailsViewer = function() {
      obj.@org.sonar.plugins.core.testdetailsviewer.client.GwtTestDetailsViewer::loadContainer()();
    }
    $wnd.on_resource_loaded_org_codehaus_sonar_plugins_testability_client_GwtTestabilityDetailsViewer = function() {
      obj.@org.sonar.plugins.core.testdetailsviewer.client.GwtTestDetailsViewer::onResourceLoaded()();
    }
  }-*/;

  @Override
  protected String getGwtId() {
    return GWT_ID;
  }

  @Override
  protected boolean isDefault(Metric metric, Resource resource) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  protected boolean isForResource(Resource resource) {
    return resource.getScope().equals(Resource.SCOPE_FILE) && resource.getQualifier().equals(Resource.QUALIFIER_CLASS);
  }

  @Override
  protected SourceDecorator<?> getDecorator(Resource resource) {
    // TODO Auto-generated method stub
    return null;
  }

}
