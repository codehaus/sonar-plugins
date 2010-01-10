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

import com.google.gwt.user.client.ui.*;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;

import java.util.*;

public class ResourceBox extends Composite {

  private Resource resource;
  VerticalPanel panel = new VerticalPanel();

  public ResourceBox(Resource resource) {
    initWidget(panel);
    this.resource = resource;
    this.setStylePrimaryName("box");
    build();
  }

  public Panel build() {
    panel.add(buildTitle());
    panel.add(buildMetadata());
    panel.add(buildMeasures());   
    return panel;
  }

  private Widget buildMeasures() {
    Grid grid = new Grid(resource.getMeasures().size(), 2);
    grid.setStyleName("subbox");
    List<Measure> sortedMeasures = sort(resource.getMeasures());
    for (int index = 0; index < sortedMeasures.size(); index++) {
      Measure measure = sortedMeasures.get(index);
      grid.setText(index, 0, measure.getMetricKey());
      if (measure.getFormattedValue() != null) {
        grid.setText(index, 1, sortedMeasures.get(index).getFormattedValue());
      } else {
        grid.setText(index, 1, "[DATA]");
      }
    }
    return grid;
  }

  private Widget buildMetadata() {
    Grid grid = new Grid(6, 2);
    grid.setStyleName("subbox");
    grid.setText(0, 0, "id:");
    grid.setText(0, 1, "" + resource.getId());
    grid.setText(1, 0, "key:");
    grid.setText(1, 1, resource.getKey());
    grid.setText(2, 0, "lang:");
    grid.setText(2, 1, resource.getLanguage());
    grid.setText(3, 0, "scope:");
    grid.setText(3, 1, resource.getScope());
    grid.setText(4, 0, "qualifier:");
    grid.setText(4, 1, resource.getQualifier());
    grid.setText(5, 0, "version:");
    grid.setText(5, 1, resource.getVersion());
    return grid;
  }

  private Label buildTitle() {
    Label title = new Label(resource.getName());
    title.setStyleName("boxt");
    return title;
  }

  private List<Measure> sort(Collection<Measure> measures) {
    List<Measure> result = new ArrayList<Measure>(measures);
    Collections.sort(result, new Comparator<Measure>() {

      public int compare(Measure m1, Measure m2) {
        if (m1.getMetricKey()==null || m2.getMetricKey()==null) {
          return -1;
        }
        return m1.getMetricKey().compareTo(m2.getMetricKey());
      }
    });

    return result;
  }
}
