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

package org.sonar.plugins.technicaldebt;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.Arrays;
import java.util.List;

/**
 * {@inheritDoc}
 */
public final class TechnicalDebtMetrics implements Metrics {

  public static final Metric TECHNICAL_DEBT = new Metric.Builder("technical_debt", "Technical Debt ($)", Metric.ValueType.INT)
      .setDescription("Technical debt ($)")
      .setDirection(Metric.DIRECTION_WORST)
      .setQualitative(false)
      .setDomain(CoreMetrics.DOMAIN_GENERAL)
      .create();

  public static final Metric TECHNICAL_DEBT_RATIO = new Metric.Builder("technical_debt_ratio", "Technical Debt ratio", Metric.ValueType.PERCENT)
      .setDescription("This is the debt ratio")
      .setDirection(Metric.DIRECTION_WORST)
      .setQualitative(true)
      .setDomain(CoreMetrics.DOMAIN_GENERAL)
      .create();

  public static final Metric TECHNICAL_DEBT_DAYS = new Metric.Builder("technical_debt_days", "Technical Debt in days", Metric.ValueType.INT)
      .setDescription("This is the technical debt of the component in man days")
      .setDirection(Metric.DIRECTION_WORST)
      .setQualitative(false)
      .setDomain(CoreMetrics.DOMAIN_GENERAL)
      .create();

  public static final Metric TECHNICAL_DEBT_REPARTITION = new Metric.Builder("technical_debt_repart", "Technical debt repartition", Metric.ValueType.DISTRIB)
      .setDescription("This is the detail of the technical debt")
      .setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false)
      .setDomain(CoreMetrics.DOMAIN_GENERAL)
      .create();

  public static final Metric TECHNICAL_DEBT_COMPLEXITY = new Metric.Builder("technical_debt_complexity", "Technical debt complexity", Metric.ValueType.FLOAT)
      .setDescription("This is the technical debt of methods and classes above thresholds")
      .setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false)
      .setDomain(CoreMetrics.DOMAIN_GENERAL)
      .setHidden(true)
      .create();

  /**
   * {@inheritDoc}
   */
  public List<Metric> getMetrics() {
    return Arrays.asList(
        TECHNICAL_DEBT,
        TECHNICAL_DEBT_DAYS,
        TECHNICAL_DEBT_REPARTITION,
        TECHNICAL_DEBT_RATIO,
        TECHNICAL_DEBT_COMPLEXITY
        );
  }
}
