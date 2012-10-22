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

package org.sonar.plugins.technicaldebt.axis;

import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Metric;

import java.util.List;

public abstract class AxisDebtCalculator {
  protected Settings settings;
  public static final double HOURS_PER_DAY = 8.0;

  public AxisDebtCalculator(Settings settings) {
    this.settings = settings;
  }

  public abstract double calculateAbsoluteDebt(DecoratorContext context);

  public abstract double calculateTotalPossibleDebt(DecoratorContext context);

  public abstract List<Metric> dependsOn();

  public abstract String getName();
}
