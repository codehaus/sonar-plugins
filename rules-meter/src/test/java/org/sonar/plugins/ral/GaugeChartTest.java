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
package org.sonar.plugins.ral;

import org.junit.Test;
import org.sonar.api.charts.AbstractChartTest;
import org.sonar.api.charts.ChartParameters;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class GaugeChartTest extends AbstractChartTest {

  @Test
  public void shouldGenerateXradar() throws IOException {
    String url = "w=120&h=150&v=67";
    GaugeChart radar = new GaugeChart();
    BufferedImage img = radar.generateImage(new ChartParameters(url));
    saveChart(img, "shouldGenerateXradar.png");
    assertChartSizeGreaterThan(img, 50);
  }

  @Test
  public void negativeValuesAreNotDisplayed() throws IOException {
    String url = "w=200&h=200&v=-90";
    GaugeChart radar = new GaugeChart();
    BufferedImage img = radar.generateImage(new ChartParameters(url));
    saveChart(img, "negativeValuesAreNotDisplayed.png");

    // you have to check visually that it does not work ! Min value is 0. This is a limitation of JFreeChart.
    assertChartSizeGreaterThan(img, 50);
  }
}