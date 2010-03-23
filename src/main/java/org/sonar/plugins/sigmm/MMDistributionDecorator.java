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

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Project;
import org.sonar.api.measures.*;

import java.util.List;
import java.util.Arrays;

public class MMDistributionDecorator implements Decorator {

  @DependedUpon
  public List<Metric> dependedUpon() {
    return Arrays.asList(MMMetrics.NCLOC_BY_NCLOC_DISTRIB, MMMetrics.NCLOC_BY_CC_DISTRIB);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    // Distribution has already been calculated by the Sensor at file level
    if (!MMPlugin.shouldPersistMeasures(resource)) {
      return;
    }
    computeAndSaveDistributionFromChildren(context, MMConfiguration.CC_DISTRIBUTION_BOTTOM_LIMITS, MMMetrics.NCLOC_BY_CC_DISTRIB);
    computeAndSaveDistributionFromChildren(context, MMConfiguration.NCLOC_DISTRIBUTION_BOTTOM_LIMITS, MMMetrics.NCLOC_BY_NCLOC_DISTRIB);
  }

  protected void computeAndSaveDistributionFromChildren(DecoratorContext context, Number[] bottomLimits, Metric metric) {
    Measure measure = computeDistributionFromChildren(context, bottomLimits, metric);
    context.saveMeasure(measure);
  }

  protected Measure computeDistributionFromChildren(DecoratorContext context, Number[] bottomLimits, Metric metric) {
    RangeDistributionBuilder builder = new RangeDistributionBuilder(metric, bottomLimits);
    for (Measure childMeasure : context.getChildrenMeasures(metric)) {
      builder.add(childMeasure);
    }
    return builder.build();
  }

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }
}
