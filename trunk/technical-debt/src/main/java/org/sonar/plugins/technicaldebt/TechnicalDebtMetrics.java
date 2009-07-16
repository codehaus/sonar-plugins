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

import org.sonar.commons.Metric;
import org.sonar.commons.Metric.ValueType;
import org.sonar.api.core.CoreMetrics;
import org.sonar.api.core.Metrics;

import java.util.ArrayList;
import java.util.List;

/** {@inheritDoc} */
public class TechnicalDebtMetrics implements Metrics {

  public final static Metric TECHNICAL_DEBT = new Metric("technical_debt", "Technical Debt ($)", "Technical debt ($)", ValueType.INT, -1, false, CoreMetrics.DOMAIN_GENERAL, false);
  public final static Metric TECHNICAL_DEBT_DAYS = new Metric("technical_debt_days", "Technical Debt in days", "This is the technical debt of the component in man days", ValueType.INT, -1, false, CoreMetrics.DOMAIN_GENERAL, false);
  public final static Metric TECHNICAL_DEBT_REPARTITION = new Metric("technical_debt_repart", "Technical technicaldebt repartition", "This is the detail of the technical debt", ValueType.DATA, 0, false, CoreMetrics.DOMAIN_GENERAL, false);

  /** {@inheritDoc} */
  public List<Metric> getMetrics() {
    List<Metric> metrics = new ArrayList<Metric>();
    metrics.add(TECHNICAL_DEBT);
    metrics.add(TECHNICAL_DEBT_DAYS);
    metrics.add(TECHNICAL_DEBT_REPARTITION);
    return metrics;
  }
}
