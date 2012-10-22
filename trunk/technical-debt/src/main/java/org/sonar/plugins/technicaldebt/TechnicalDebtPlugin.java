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

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;

import java.util.Arrays;
import java.util.List;

@Properties({
  @Property(
    key = TechnicalDebtPlugin.DAILY_RATE,
    defaultValue = "" + TechnicalDebtPlugin.DAILY_RATE_DEFVAL,
    name = "Daily rate of a developer (in $)",
    type = PropertyType.FLOAT
  ),
  @Property(
    key = TechnicalDebtPlugin.COMPLEXITY_THRESHOLDS,
    defaultValue = TechnicalDebtPlugin.COMPLEXITY_THRESHOLDS_DEFVAL,
    name = "Maximum complexity above which component should be broken",
    global = true,
    project = true
  ),
  @Property(
    key = TechnicalDebtPlugin.COST_CLASS_COMPLEXITY,
    defaultValue = "" + TechnicalDebtPlugin.COST_CLASS_COMPLEXITY_DEFVAL,
    name = "Average time to split a class that has a too high complexity (in hours)",
    type = PropertyType.FLOAT
  ),
  @Property(
    key = TechnicalDebtPlugin.COST_METHOD_COMPLEXITY,
    defaultValue = "" + TechnicalDebtPlugin.COST_METHOD_COMPLEXITY_DEFVAL,
    name = "Average time to split a method that has a too high complexity (in hours)",
    type = PropertyType.FLOAT
  ),
  @Property(
    key = TechnicalDebtPlugin.COST_DUPLICATED_BLOCKS,
    defaultValue = "" + TechnicalDebtPlugin.COST_DUPLICATED_BLOCKS_DEFVAL,
    name = "Average time to fix one block duplication block (in hours)",
    type = PropertyType.FLOAT
  ),
  @Property(
    key = TechnicalDebtPlugin.COST_VIOLATION,
    defaultValue = "" + TechnicalDebtPlugin.COST_VIOLATION_DEFVAL,
    name = "Average time to fix a coding violation (in hours)",
    type = PropertyType.FLOAT
  ),
  @Property(
    key = TechnicalDebtPlugin.COST_UNCOVERED_COMPLEXITY,
    defaultValue = "" + TechnicalDebtPlugin.COST_UNCOVERED_COMPLEXITY_DEFVAL,
    name = "Average time to cover complexity of one (in hours)",
    type = PropertyType.FLOAT
  ),
  @Property(
    key = TechnicalDebtPlugin.COST_UNDOCUMENTED_API,
    defaultValue = "" + TechnicalDebtPlugin.COST_UNDOCUMENTED_API_DEFVAL,
    name = "Average time to document 1 API (in hours)",
    type = PropertyType.FLOAT
  ),
  @Property(
    key = TechnicalDebtPlugin.COST_CYCLE,
    defaultValue = "" + TechnicalDebtPlugin.COST_CYCLE_DEFVAL,
    name = "Average time to cut a dependency between two files (in hours)",
    type = PropertyType.FLOAT
  )
})
public final class TechnicalDebtPlugin extends SonarPlugin {
  public static final String DAILY_RATE = "technicaldebt.daily.rate";
  public static final double DAILY_RATE_DEFVAL = 500.0;

  public static final String COST_CLASS_COMPLEXITY = "technicaldebt.split.class";
  public static final double COST_CLASS_COMPLEXITY_DEFVAL = 8.0;

  public static final String COST_METHOD_COMPLEXITY = "technicaldebt.split.meth";
  public static final double COST_METHOD_COMPLEXITY_DEFVAL = 0.5;

  public static final String COST_DUPLICATED_BLOCKS = "technicaldebt.dupli.blocks";
  public static final double COST_DUPLICATED_BLOCKS_DEFVAL = 2.0;

  public static final String COST_VIOLATION = "technicaldebt.violation";
  public static final double COST_VIOLATION_DEFVAL = 0.1;

  public static final String COST_UNCOVERED_COMPLEXITY = "technicaldebt.uncovered.complexity";
  public static final double COST_UNCOVERED_COMPLEXITY_DEFVAL = 0.2;

  public static final String COST_UNDOCUMENTED_API = "technicaldebt.undocumented.api";
  public static final double COST_UNDOCUMENTED_API_DEFVAL = 0.2;

  public static final String COST_CYCLE = "technicaldebt.cut.cycle";
  public static final double COST_CYCLE_DEFVAL = 4.0;

  public static final String COMPLEXITY_THRESHOLDS = "technicaldebt.complexity.max";
  public static final String COMPLEXITY_THRESHOLDS_DEFVAL = "CLASS=60;METHOD=8";

  /**
   * {@inheritDoc}
   */
  public List getExtensions() {
    return Arrays.asList(
        TechnicalDebtMetrics.class,
        TechnicalDebtDecorator.class,
        TechnicalDebtWidget.class,
        ComplexityDebtDecorator.class
        );
  }
}
