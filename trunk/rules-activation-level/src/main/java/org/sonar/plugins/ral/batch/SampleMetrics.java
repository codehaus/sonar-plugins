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
package org.sonar.plugins.ral.batch;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.Arrays;
import java.util.List;

public class SampleMetrics implements Metrics {

  public static final Metric TODO =
      new Metric("todo", "To do", "Number of 'todo' tags", Metric.ValueType.INT, 1, Boolean.FALSE, CoreMetrics.DOMAIN_SIZE);

  public static final Metric META =
      new Metric("meta", "Meta", "Meta metric calculated from basic measures", Metric.ValueType.PERCENT, 1, Boolean.TRUE, CoreMetrics.DOMAIN_GENERAL);

  public static final Metric TEXT_MESSAGE =
      new Metric("text_message", "Text message", "Text message", Metric.ValueType.STRING, 0, Boolean.FALSE, "Sample");

  public static final Metric RANDOM_LEVEL =
      new Metric("random_level", "Random level", "Random level", Metric.ValueType.LEVEL, 1, Boolean.TRUE, CoreMetrics.DOMAIN_GENERAL);

  public static final Metric RANDOM_BOOLEAN =
      new Metric("random_boolean", "Random boolean", "Random boolean", Metric.ValueType.BOOL, 0, Boolean.FALSE, CoreMetrics.DOMAIN_GENERAL);

  public static final Metric DISTRIBUTION =
      new Metric("sample_distribution", "Distribution", "Distribution", Metric.ValueType.DISTRIB, 0, Boolean.FALSE, CoreMetrics.DOMAIN_GENERAL);

  public List<Metric> getMetrics() {
    return Arrays.asList(META, TODO, TEXT_MESSAGE, RANDOM_LEVEL, RANDOM_BOOLEAN, DISTRIBUTION);
  }
}
