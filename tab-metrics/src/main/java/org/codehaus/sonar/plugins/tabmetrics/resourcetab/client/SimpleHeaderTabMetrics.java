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

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;

/**
 * Load measures to show and print information with an structure
 * 
 */
public class SimpleHeaderTabMetrics extends Composite {

  private final FlowPanel headerTabMetrics;
  private static final int BUFFERSIZE = 512;

  /**
   * Constructor
   * 
   * @param domainName
   * @param metricsList
   */
  public SimpleHeaderTabMetrics(String domainName, List<MetricTab> metricsList) {
    super();

    headerTabMetrics = new FlowPanel();
    headerTabMetrics.setStyleName("gwt-ViewerHeader");

    // Init Widget
    super.initWidget(headerTabMetrics);

    // Change the style (Sonar style).
    headerTabMetrics.setStyleName("tab_header");

    // Show data in the tab
    printData(domainName, metricsList);
  }

  /**
   * @return the headerTabMetrics
   */
  public final FlowPanel getHeaderTabMetrics() {
    return headerTabMetrics;
  }

  /**
   * Show data in the tab
   * 
   * @param domainName
   * @param metricsList
   */
  private void printData(String domainName, List<MetricTab> metricsList) {

    // TITLE
    String titleHtml = "<h3>" + domainName + "</h3>";
    addCell(headerTabMetrics, titleHtml);

    StringBuffer buffer = new StringBuffer(BUFFERSIZE);

    // TABLE with all metrics
    buffer.append("<table width=\"100%\"><tbody>");

    for (int i = 0; i < metricsList.size(); i++) {
      buffer.append("<tr><td nowrap width=\"25%\">");
      buffer.append(createCell(metricsList.get(i)));
      buffer.append("</td><td width=\"75%\"><span style=\"font-style:italic\">" + metricsList.get(i).getDescription() + "</span></td></tr>");
    }

    buffer.append("</tbody></table>");

    addCell(headerTabMetrics, buffer.toString());
  }

  /**
   * Returns HTML code for metric's name and its value
   * 
   * @param metricTab
   */
  protected final String createCell(MetricTab metricTab) {
    StringBuffer buffer = new StringBuffer(BUFFERSIZE);

    // Name
    buffer.append(metricTab.getName());

    // Value with bold type
    buffer.append(": <b>");
    buffer.append(metricTab.getValue());
    buffer.append("</b>");

    return buffer.toString();
  }

  /**
   * Add HTML code in a cell
   * 
   * @param panel
   * @param html
   *          HTML code
   */
  protected final void addCell(Panel panel, String html) {
    HTML htmlDiv = new HTML(html);
    panel.add(htmlDiv);
  }
}
