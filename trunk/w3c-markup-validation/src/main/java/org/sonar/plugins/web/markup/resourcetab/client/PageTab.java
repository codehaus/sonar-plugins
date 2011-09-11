/*
 * Sonar W3C Markup Validation Plugin
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.web.markup.resourcetab.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.sonar.gwt.ui.Loading;
import org.sonar.gwt.ui.Page;
import org.sonar.wsclient.gwt.AbstractCallback;
import org.sonar.wsclient.gwt.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PageTab extends Page {

  private VerticalPanel panel;

  @Override
  protected Widget doOnResourceLoad(Resource resource) {
    panel = new VerticalPanel();
    panel.setWidth("100%");
    loadData(resource);
    return panel;
  }

  private void loadData(Resource resource) {
    panel.add(new Loading());

    ResourceQuery query = ResourceQuery.createForResource(resource, "page_metrics");
    Sonar.getInstance().find(query, new AbstractCallback<Resource>() {

      @Override
      protected void doOnResponse(Resource result) {
        panel.clear();
        Measure measure = result.getMeasure("page_metrics");
        if (measure == null || measure.getData() == null) {
          panel.add(new Label("No value"));
        } else {

          Map<String, String> properties = parseData(measure.getData());

          // url in header
          panel.add(createHeader(properties.get("url")));
          properties.remove("key");

          // properties in form
          Grid grid = new Grid(properties.size(), 2);
          grid.setStyleName("form");

          int n = 0;
          for (Entry<String, String> entry : properties.entrySet()) {
            grid.setWidget(n, 0, new Label(entry.getKey().toString()));
            grid.setWidget(n, 1, new Label(entry.getValue()));

            n++;
          }

          panel.add(grid);
        }
      }
    });
  }

  private Map<String, String> parseData(String data) {
    Map<String, String> map = new HashMap<String, String>();

    String[] props = data.split("\n");
    for (String prop : props) {
      int eq = prop.indexOf('=');
      if (eq > 0) {
        map.put(prop.substring(0, eq), prop.substring(eq + 1));
      }
    }
    return map;
  }


  private FlowPanel createHeader(String url) {
      FlowPanel header = new FlowPanel();
      header.setStyleName("gwt-ViewerHeader");

      HorizontalPanel horizPanel = new HorizontalPanel();
      HTML html = new HTML("URL: ");
      html.setStyleName("metric");
      horizPanel.add(html);

      if (url != null) {
        FlowPanel cell = new FlowPanel();
        cell.setStyleName("value");
        Anchor anchor = new Anchor(url, url);

        cell.add(anchor);
        horizPanel.add(cell);
      }

      header.add(horizPanel);
      return header;
  }
}