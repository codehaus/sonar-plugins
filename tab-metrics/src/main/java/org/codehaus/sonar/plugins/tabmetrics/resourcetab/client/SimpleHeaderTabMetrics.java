/*
 * Sonar Tab Metrics Plugin
 * Copyright (C) 2012 eXcentia
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.codehaus.sonar.plugins.tabmetrics.resourcetab.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Load measures to show and print information with an structure
 * 
 */
public class SimpleHeaderTabMetrics extends Composite {

  private final FlowPanel headerTabMetrics;
  private List<MetricTab> metricsList;
  private final String domainName;

  /**
   * Constructor
   * 
   * @param domainName
   * @param metricsList
   */
  public SimpleHeaderTabMetrics(String domainName) {
    super();

    this.domainName = domainName;
    this.metricsList = new ArrayList<MetricTab>();

    headerTabMetrics = new FlowPanel();
    headerTabMetrics.setStyleName("gwt-ViewerHeader");

    // Init Widget
    super.initWidget(headerTabMetrics);

    // Change the style (Sonar style).
    headerTabMetrics.setStyleName("tab_header");
  }

  /**
   * @return the headerTabMetrics
   */
  public final FlowPanel getHeaderTabMetrics() {
    return headerTabMetrics;
  }

  /**
   * @return the metricsList
   */
  public final List<MetricTab> getMetricsList() {
    return metricsList;
  }

  /**
   * @param metricsList
   *          the metricsList to set
   */
  public final void setMetricsList(List<MetricTab> metricsList) {
    this.metricsList = metricsList;
  }

  /**
   * @return the domainName
   */
  public final String getDomainName() {
    return domainName;
  }

  /**
   * Show data in the tab
   * 
   * @param domainName
   * @param metricsList
   */
  public final void printData() {

    // TITLE
    String titleHtml = "<h3>" + domainName + "</h3>";
    TabUtil.addCell(headerTabMetrics, titleHtml);

    StringBuffer buffer = new StringBuffer(TabUtil.BUFFERSIZE);

    // TABLE with all metrics
    buffer.append("<table width=\"100%\"><tbody>");

    for (int i = 0; i < metricsList.size(); i++) {
      buffer.append("<tr><td nowrap width=\"25%\">");
      buffer.append(TabUtil.createCell(metricsList.get(i)));
      buffer.append("</td><td width=\"75%\"><span style=\"font-style:italic\">" + metricsList.get(i).getDescription() + "</span></td></tr>");
    }

    buffer.append("</tbody></table>");

    TabUtil.addCell(headerTabMetrics, buffer.toString());
  }
}
