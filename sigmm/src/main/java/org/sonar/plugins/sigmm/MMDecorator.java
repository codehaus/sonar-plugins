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

import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Project;
import org.sonar.plugins.sigmm.axis.MMAxis;

import java.util.*;

/**
 * {@inheritDoc}
 */
public final class MMDecorator implements org.sonar.api.batch.Decorator {

  /**
   * {@inheritDoc}
   */
  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  @DependsUpon
  public List<Metric> dependsUpon() {
    return Arrays.asList(CoreMetrics.NCLOC, CoreMetrics.DUPLICATED_LINES_DENSITY, CoreMetrics.COVERAGE,
      MMMetrics.NCLOC_BY_CC_DISTRIB, MMMetrics.NCLOC_BY_NCLOC_DISTRIB);
  }

  @DependedUpon
  public List<Metric> dependedUpon() {
    return Arrays.asList(MMMetrics.ANALYSABILITY, MMMetrics.CHANGEABILITY, MMMetrics.TESTABILITY, MMMetrics.STABILITY,
      MMMetrics.MAINTAINABILIY);
  }

  /**
   * {@inheritDoc}
   */
  public void decorate(Resource resource, DecoratorContext context) {
    if (!MMPlugin.shouldPersistMeasures(resource)) {
      return;
    }
    MMConfiguration config = new MMConfiguration(context);

    saveMeasure(context, config.getTestabilityAxis(), MMMetrics.TESTABILITY);
    saveMeasure(context, config.getStabilityAxis(), MMMetrics.STABILITY);
    saveMeasure(context, config.getChangeabilityAxis(), MMMetrics.CHANGEABILITY);
    saveMeasure(context, config.getAnalysibilityAxis(), MMMetrics.ANALYSABILITY);
    saveMeasure(context, config.getMaintainability(), MMMetrics.MAINTAINABILIY);
  }

  private void saveMeasure(DecoratorContext context, MMAxis axis, Metric metric) {
    MMRank rank = axis.getRank();
    if (rank != null) {
      context.saveMeasure(metric, rank.getValue());
    }
  }
}
