/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
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

package org.sonar.plugins.taglist;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.Arrays;
import java.util.List;

public class TaglistMetrics implements Metrics {

  public final static Metric TAGS = new Metric(
      "tags", "Tags", "Number of tags in the source code",
      Metric.ValueType.INT, Metric.DIRECTION_WORST, true, CoreMetrics.DOMAIN_RULES);
  public final static Metric MANDATORY_TAGS = new Metric(
      "mandatory_tags", "Mandatory tags", "Number of mandatory tags in the source code",
      Metric.ValueType.INT, Metric.DIRECTION_WORST, true, CoreMetrics.DOMAIN_RULES);
  public final static Metric OPTIONAL_TAGS = new Metric(
      "optional_tags", "Optional tags", "Number of optional tags in the source code",
      Metric.ValueType.INT, Metric.DIRECTION_WORST, true, CoreMetrics.DOMAIN_RULES);
  public final static Metric TAGS_DISTRIBUTION = new Metric(
      "tags_distribution", "Tags distribution", "Distribution of tags in the source code",
      Metric.ValueType.DISTRIB, Metric.DIRECTION_NONE, false, CoreMetrics.DOMAIN_RULES);

  public List<Metric> getMetrics() {
    return Arrays.asList(TAGS, MANDATORY_TAGS, OPTIONAL_TAGS, TAGS_DISTRIBUTION);
  }
}
