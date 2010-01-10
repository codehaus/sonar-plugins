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
import org.sonar.api.web.gwt.client.AbstractPage;
import org.sonar.api.web.gwt.client.ResourceDictionary;
import org.sonar.wsclient.gwt.AbstractListCallback;
import org.sonar.wsclient.gwt.Sonar;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import java.util.List;

public class DebugPage extends AbstractPage {
  public static final String GWT_ID = "org.sonar.plugins.debug.page.DebugPage";

  private HorizontalPanel columnsPanel = new HorizontalPanel();

  public void onModuleLoad() {
    columnsPanel.setStylePrimaryName("debug");
    loadData();
    displayView(columnsPanel);
  }

  private void loadData() {
    String resourceKey = ResourceDictionary.getResourceKey();

    loadColumn(resourceKey, 0);
    loadColumn(resourceKey, 1);
    loadColumn(resourceKey, 2);
  }

  private void loadColumn(String resourceKey, int depth) {
    Sonar.getInstance().findAll(ResourceQuery.build(resourceKey, "true").setVerbose(true).setDepth(depth), new AbstractListCallback<Resource>() {
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
