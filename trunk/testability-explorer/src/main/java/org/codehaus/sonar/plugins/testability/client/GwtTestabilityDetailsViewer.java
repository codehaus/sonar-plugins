/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

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
