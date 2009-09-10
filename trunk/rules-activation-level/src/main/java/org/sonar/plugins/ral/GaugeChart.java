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

import org.jfree.chart.plot.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.general.ValueDataset;
import org.jfree.data.general.DefaultValueDataset;
import org.sonar.api.charts.AbstractChart;
import org.sonar.api.charts.ChartParameters;

import java.awt.Color;
import java.text.NumberFormat;

public class GaugeChart extends AbstractChart {
  public static final String PARAM_VALUES = "v";

  public String getKey() {
    return "thermometer";
  }

  @Override
  protected Plot getPlot(ChartParameters params) {
    ThermometerPlot plot = new ThermometerPlot(createDataset(params));

    plot.setGap(1);

    plot.setRange(0, 1);
    plot.setSubrange(0, 0, 1);

    plot.setSubrangePaint(0, Color.decode("#ffffff"));
    plot.setSubrangePaint(1, Color.decode("#ffffff"));
    plot.setSubrangePaint(2, Color.decode("#ffffff"));

    plot.setMercuryPaint(Color.decode("#dddddd"));
    plot.setThermometerPaint(Color.decode("#aaaaaa"));
    plot.setValuePaint(Color.decode("#333333"));

    plot.setUseSubrangePaint(false);
    plot.setUnits(ThermometerPlot.UNITS_NONE);
    plot.setBulbRadius(15);
    plot.setColumnRadius(6);

    NumberAxis axis = new NumberAxis();
    axis.setNumberFormatOverride(NumberFormat.getPercentInstance());
    plot.setRangeAxis(axis);
    plot.setValueLocation(ThermometerPlot.NONE);
    
    return plot;
  }


  private ValueDataset createDataset(ChartParameters params) {
    String value = params.getValue(PARAM_VALUES);
    DefaultValueDataset set = new DefaultValueDataset(Double.valueOf(value) / 100);
    return set;
  }


}