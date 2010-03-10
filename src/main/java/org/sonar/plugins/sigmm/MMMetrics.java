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
package org.sonar.plugins.sigmm;


import org.sonar.api.measures.Metric;
import org.sonar.api.measures.CoreMetrics;

import java.util.Arrays;
import java.util.List;

/**
 * {@inheritDoc}
 */
public final class MMMetrics implements org.sonar.api.measures.Metrics {
  public static final Metric ANALYSABILITY = new Metric("sigmm-analysability", "Analysability Value", "Analysability in an interval of [--, ++]", Metric.ValueType.INT, 1, true, CoreMetrics.DOMAIN_GENERAL);
  public static final Metric CHANGEABILITY = new Metric("sigmm-changeability", "Changeability Value", "Changeability in an interval of [--, ++]", Metric.ValueType.INT, 1, true, CoreMetrics.DOMAIN_GENERAL);
  public static final Metric TESTABILITY = new Metric("sigmm-testability", "Testability Value", "Testability in an interval of [--, ++]", Metric.ValueType.INT, 1, true, CoreMetrics.DOMAIN_GENERAL);
  public static final Metric STABILITY = new Metric("sigmm-stability", "Stability Value", "Stability in an interval of [--, ++]", Metric.ValueType.INT, 1, true, CoreMetrics.DOMAIN_GENERAL);
  public static final Metric MAINTAINABILIY = new Metric("sigmm-maintainability", "SIG MM", "Maintainability in an interval of [--, ++]", Metric.ValueType.INT, 1, true, CoreMetrics.DOMAIN_GENERAL);

  public static final Metric NCLOC_BY_CC_DISTRIB = new Metric("sigmm-ncloc-by-cc", "SIG NCLOC by CC", "Repartition of the ncloc on cc range", Metric.ValueType.DISTRIB, -1, false, CoreMetrics.DOMAIN_GENERAL);
  public static final Metric NCLOC_BY_NCLOC_DISTRIB = new Metric("sigmm-ncloc-by-ncloc", "SIG NCLOC by CC", "Repartition of the ncloc on ncloc range", Metric.ValueType.DISTRIB, -1, false, CoreMetrics.DOMAIN_GENERAL);
  /**
   * {@inheritDoc}
   */
  public List<Metric> getMetrics() {
    return Arrays.asList(ANALYSABILITY, CHANGEABILITY, TESTABILITY, STABILITY, MAINTAINABILIY, NCLOC_BY_CC_DISTRIB, NCLOC_BY_NCLOC_DISTRIB);
  }
}
