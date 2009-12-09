package org.codehaus.sonar.plugins.testability.client;

import org.codehaus.sonar.plugins.testability.client.webservices.WSTestabilityMetrics;
import org.sonar.api.web.gwt.client.AbstractViewer;
import org.sonar.api.web.gwt.client.webservices.Resource;
import org.sonar.api.web.gwt.client.webservices.WSMetrics.Metric;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class GwtTestabilityDetailsViewer extends AbstractViewer {
  
  public static final String GWT_ID = "org.codehaus.sonar.plugins.testability.GwtTestabilityDetailsViewer";
  
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
    return isMetricInList(metric, WSTestabilityMetrics.METHOD_DETAILS_COST, WSTestabilityMetrics.TESTABILITY_COST);
  }

  @Override
  protected boolean isForResource(Resource resource) {
    return resource.getScope().equals(Resource.SCOPE_ENTITY) && resource.getQualifier().equals(Resource.QUALIFIER_CLASS);
  }

	@Override
	protected Widget render(Resource resource) {
	  FlowPanel panel = new FlowPanel();
	  panel.setWidth("100%");
	  panel.add(new GwtTestabilitySourceHeader(resource));
	  panel.add(new GwtTestabilitySourcePanel(resource));
	  return panel;
	}

}
