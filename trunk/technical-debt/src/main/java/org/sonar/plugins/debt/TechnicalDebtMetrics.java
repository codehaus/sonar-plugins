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
package org.sonar.plugins.debt;

import java.util.ArrayList;
import java.util.List;

import org.sonar.commons.Metric;
import org.sonar.commons.Metric.ValueType;
import org.sonar.plugins.api.metrics.CoreMetrics;
import org.sonar.plugins.api.metrics.Metrics;

public class TechnicalDebtMetrics implements Metrics {

    public final static Metric TOTAL_TECHNICAL_DEBT = new Metric("total_tech_debt", "Total Technical Debt", "This is the technical debt of the component", ValueType.INT, -1, false, CoreMetrics.DOMAIN_GENERAL, false);
    public final static Metric SONAR_TECHNICAL_DEBT = new Metric("sonar_tech_debt", "Sonar Technical Debt", "This represents the debt calculated by Sonar", ValueType.INT, -1, false, CoreMetrics.DOMAIN_GENERAL, false);
    public final static Metric EXTRA_TECHNICAL_DEBT = new Metric("extra_tech_debt", "Extra Technical Debt", "This represents the debt entered manually in the system", ValueType.INT, -1, false, CoreMetrics.DOMAIN_GENERAL, false);

    public List<Metric> getMetrics() {
        List<Metric> metrics = new ArrayList<Metric>();
        metrics.add(TOTAL_TECHNICAL_DEBT);
        metrics.add(SONAR_TECHNICAL_DEBT);
        metrics.add(EXTRA_TECHNICAL_DEBT);
        return metrics;
    }
}
