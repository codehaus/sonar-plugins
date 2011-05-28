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

package org.sonar.plugins.secrules;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.Arrays;
import java.util.List;

public final class SecurityRulesMetrics implements Metrics {
  public final static Metric SECURITY_VIOLATIONS = new Metric("security-violations", "Security violations",
      "Security violations",
      Metric.ValueType.INT,
      Metric.DIRECTION_WORST,
      true,
      CoreMetrics.DOMAIN_RULES)
      .setBestValue(0.0)
      .setOptimizedBestValue(true);

  public final static Metric WEIGHTED_SECURITY_VIOLATIONS = new Metric("weighted-security-violations", "Weighted Security Violations",
      "Weighted Security Violations",
      Metric.ValueType.INT,
      Metric.DIRECTION_WORST,
      true,
      CoreMetrics.DOMAIN_RULES)
      .setBestValue(0.0)
      .setOptimizedBestValue(true);

  public final static Metric SECURITY_RCI = new Metric("security-rci", "Security rules compliance",
      "Security rules compliance",
      Metric.ValueType.PERCENT,
      Metric.DIRECTION_BETTER,
      true, CoreMetrics.DOMAIN_RULES)
      .setBestValue(100.0)
      .setOptimizedBestValue(true);

  public final static Metric SECURITY_VIOLATIONS_DISTRIBUTION = new Metric("security-violations-distribution", "Security Violations Distribution",
      "Security Violations Distribution",
      Metric.ValueType.DATA,
      Metric.DIRECTION_NONE,
      false,
      CoreMetrics.DOMAIN_RULES);

  public List<Metric> getMetrics() {
    return Arrays.asList(SECURITY_VIOLATIONS, WEIGHTED_SECURITY_VIOLATIONS, SECURITY_RCI, SECURITY_VIOLATIONS_DISTRIBUTION);
  }
}
