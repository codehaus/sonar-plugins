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

package org.sonar.plugins.sigmm;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;

import java.util.Arrays;
import java.util.List;

/**
 * {@inheritDoc}
 */
public final class MMMetrics implements org.sonar.api.measures.Metrics {

  public static final Metric ANALYSABILITY = new Metric.Builder("sigmm-analysability", "Analysability Value", Metric.ValueType.INT)
      .setDescription("Analysability in an interval of [--, ++]")
      .setDirection(Metric.DIRECTION_BETTER)
      .setQualitative(true)
      .setDomain(CoreMetrics.DOMAIN_GENERAL)
      .create();

  public static final Metric CHANGEABILITY = new Metric.Builder("sigmm-changeability", "Changeability Value", Metric.ValueType.INT)
      .setDescription("Changeability in an interval of [--, ++]")
      .setDirection(Metric.DIRECTION_BETTER)
      .setQualitative(true)
      .setDomain(CoreMetrics.DOMAIN_GENERAL)
      .create();

  public static final Metric TESTABILITY = new Metric.Builder("sigmm-testability", "Testability Value", Metric.ValueType.INT)
      .setDescription("Testability in an interval of [--, ++]")
      .setDirection(Metric.DIRECTION_BETTER)
      .setQualitative(true)
      .setDomain(CoreMetrics.DOMAIN_GENERAL)
      .create();

  public static final Metric STABILITY = new Metric.Builder("sigmm-stability", "Stability Value", Metric.ValueType.INT)
      .setDescription("Stability in an interval of [--, ++]")
      .setDirection(Metric.DIRECTION_BETTER)
      .setQualitative(true)
      .setDomain(CoreMetrics.DOMAIN_GENERAL)
      .create();

  public static final Metric MAINTAINABILIY = new Metric.Builder("sigmm-maintainability", "SIG MM", Metric.ValueType.INT)
      .setDescription("Maintainability in an interval of [--, ++]")
      .setDirection(Metric.DIRECTION_BETTER)
      .setQualitative(true)
      .setDomain(CoreMetrics.DOMAIN_GENERAL)
      .create();

  public static final Metric NCLOC_BY_CC_DISTRIB = new Metric.Builder("sigmm-ncloc-by-cc", "SIG NCLOC by CC", Metric.ValueType.DISTRIB)
      .setDescription("Repartition of the ncloc on cc range")
      .setDirection(Metric.DIRECTION_WORST)
      .setQualitative(false)
      .setDomain(CoreMetrics.DOMAIN_GENERAL)
      .create();

  public static final Metric NCLOC_BY_NCLOC_DISTRIB = new Metric.Builder("sigmm-ncloc-by-ncloc", "SIG NCLOC by NCLOC", Metric.ValueType.DISTRIB)
      .setDescription("Repartition of the ncloc on ncloc range")
      .setDirection(Metric.DIRECTION_WORST)
      .setQualitative(false)
      .setDomain(CoreMetrics.DOMAIN_GENERAL)
      .create();

  /**
   * {@inheritDoc}
   */
  public List<Metric> getMetrics() {
    return Arrays.asList(ANALYSABILITY, CHANGEABILITY, TESTABILITY, STABILITY, MAINTAINABILIY, NCLOC_BY_CC_DISTRIB, NCLOC_BY_NCLOC_DISTRIB);
  }
}
