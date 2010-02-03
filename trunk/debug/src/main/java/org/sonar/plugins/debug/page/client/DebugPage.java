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
package org.sonar.plugins.debug.page.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.sonar.gwt.ui.Page;
import org.sonar.wsclient.gwt.AbstractListCallback;
import org.sonar.wsclient.gwt.Sonar;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import java.util.List;

public class DebugPage extends Page {
  public static final String GWT_ID = "org.sonar.plugins.debug.page.DebugPage";

  private HorizontalPanel columnsPanel = new HorizontalPanel();

  @Override
  protected Widget doOnResourceLoad(Resource resource) {
    columnsPanel.setStylePrimaryName("debug");
    loadData(resource);
    return columnsPanel;
  }

  private void loadData(Resource resource) {
    loadColumn(resource, 0);
    loadColumn(resource, 1);
    loadColumn(resource, 2);
  }

  private void loadColumn(Resource resource, int depth) {
    ResourceQuery query = ResourceQuery.createForMetrics(resource.getId().toString(), "true")
        .setVerbose(true)
        .setDepth(depth);

    Sonar.getInstance().findAll(query, new AbstractListCallback<Resource>() {
      @Override
      protected void doOnResponse(final List<Resource> resources) {
        VerticalPanel column = new VerticalPanel();
        column.setStyleName("col");
        for (Resource resource : resources) {
          ResourceBox box = new ResourceBox(resource);
          column.add(box);
        }
        columnsPanel.add(column);
      }
    });
  }

}
