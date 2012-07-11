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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;

/**
 * Tab Utility Class
 */
public final class TabUtil {

  public static final int BUFFERSIZE = 512;
  public static final int MAXLENGTH = 24;

  /**
   * The only instance to be created
   */
  private static TabUtil instance = new TabUtil();

  /**
   * Private constructor (Singleton pattern)
   */
  private TabUtil() {
  }

  /**
   * Obtains TabUtil instance
   * 
   * @return: the instance
   */
  public static TabUtil getInstance() {
    return instance;
  }

  /**
   * Returns HTML code for metric's name and its value
   * 
   * @param metricTab
   */
  public static String createCell(MetricTab metricTab) {
    StringBuffer buffer = new StringBuffer(BUFFERSIZE);

    // Name
    buffer.append(metricTab.getName());

    // Value with bold type
    buffer.append(": ");

    // NUMERIC
    if (metricTab.isNumeric()) {
      buffer.append("<b>" + metricTab.getValue() + "</b>");
    }
    // DATA
    else {
      String data = metricTab.getData();

      buffer.append("<input type=\"text\" style=\"background-color: #EFEFEF; font-weight: bold\" readonly=\"readonly\" size=\"" + MAXLENGTH
          + "\" value=\"" + data + "\"/>");
    }

    return buffer.toString();
  }

  /**
   * Add HTML code in a cell
   * 
   * @param panel
   * @param html
   *          HTML code
   */
  public static void addCell(Panel panel, String html) {
    HTML htmlDiv = new HTML(html);
    panel.add(htmlDiv);
  }
}
