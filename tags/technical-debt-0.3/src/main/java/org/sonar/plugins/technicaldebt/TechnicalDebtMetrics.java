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
package org.sonar.plugins.technicaldebt;



import org.sonar.api.measures.Metrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.CoreMetrics;

import java.util.Arrays;
import java.util.List;

/**
 * {@inheritDoc}
 */
public final class TechnicalDebtMetrics implements Metrics {

  public static final  Metric TECHNICAL_DEBT = new Metric("technical_debt", "Technical Debt ($)", "Technical debt ($)", Metric.ValueType.INT, -1, false, CoreMetrics.DOMAIN_GENERAL);
  public static final Metric TECHNICAL_DEBT_RATIO = new Metric("technical_debt_ratio", "Technical Debt ratio", "This is the debt ratio", Metric.ValueType.PERCENT, -1, false, CoreMetrics.DOMAIN_GENERAL);
  public static final Metric TECHNICAL_DEBT_DAYS = new Metric("technical_debt_days", "Technical Debt in days", "This is the technical debt of the component in man days", Metric.ValueType.INT, -1, false, CoreMetrics.DOMAIN_GENERAL);
  public static final Metric TECHNICAL_DEBT_REPARTITION = new Metric("technical_debt_repart", "Technical technicaldebt repartition", "This is the detail of the technical debt", Metric.ValueType.DATA, 0, false, CoreMetrics.DOMAIN_GENERAL);

  /**
   * {@inheritDoc}
   */
  public List<Metric> getMetrics() {
    return Arrays.asList(TECHNICAL_DEBT, TECHNICAL_DEBT_DAYS, TECHNICAL_DEBT_REPARTITION, TECHNICAL_DEBT_RATIO);
  }
}
