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
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.plugins.sigmm.axis.AdvancedAxis;
import org.sonar.plugins.sigmm.axis.MMAxis;
import org.sonar.plugins.sigmm.axis.SimpleAxis;
import org.sonar.plugins.sigmm.axis.CombinedAxis;

public class MMConfiguration {

  private static int[] volumeLimits = {1310000, 655000, 246000, 66000, 0};
  private static int[] duplicationLimits = {20, 10, 5, 3, 0};
  private static int[] coverageLimits = {95, 80, 60, 20, 0};
  public static Number[] CC_DISTRIBUTION_BOTTOM_LIMITS = {50, 20, 10, 0};
  public static Number[] NCLOC_DISTRIBUTION_BOTTOM_LIMITS = {100, 50, 10, 0};

  private MMAxis testabilityAxis;

  private MMAxis stabilityAxis;
  private MMAxis changeabilityAxis;
  private MMAxis analysibilityAxis;

  private MMAxis maintainability;

  public MMConfiguration(DecoratorContext context) {
    MMAxis volumeAxis = new SimpleAxis(volumeLimits, MMRank.ascSortedRanks(), CoreMetrics.NCLOC, context);
    MMAxis duplicationAxis = new SimpleAxis(duplicationLimits, MMRank.ascSortedRanks(), CoreMetrics.DUPLICATED_LINES_DENSITY, context);
    MMAxis coverageAxis = new SimpleAxis(coverageLimits, MMRank.descSortedRanks(), CoreMetrics.COVERAGE, context);
    MMAxis complexityAxis = new AdvancedAxis(CC_DISTRIBUTION_BOTTOM_LIMITS, context, MMMetrics.NCLOC_BY_CC_DISTRIB);
    MMAxis unitSizeAxis = new AdvancedAxis(NCLOC_DISTRIBUTION_BOTTOM_LIMITS, context, MMMetrics.NCLOC_BY_NCLOC_DISTRIB);

    testabilityAxis = new CombinedAxis(complexityAxis, unitSizeAxis, coverageAxis);
    stabilityAxis = new CombinedAxis(coverageAxis);
    changeabilityAxis = new CombinedAxis(complexityAxis, duplicationAxis);
    analysibilityAxis = new CombinedAxis(volumeAxis, duplicationAxis, unitSizeAxis, coverageAxis);
    maintainability = new CombinedAxis(testabilityAxis, stabilityAxis, changeabilityAxis, analysibilityAxis);
  }

  public MMAxis getMaintainability() {
    return maintainability;
  }

  public MMAxis getAnalysibilityAxis() {
    return analysibilityAxis;
  }

  public MMAxis getChangeabilityAxis() {
    return changeabilityAxis;
  }

  public MMAxis getStabilityAxis() {
    return stabilityAxis;
  }

  public MMAxis getTestabilityAxis() {
    return testabilityAxis;
  }

  
}
