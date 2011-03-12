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

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Project;
import org.sonar.api.measures.*;
import org.sonar.api.resources.Scopes;

import java.util.List;
import java.util.Arrays;

public class MMDistributionDecorator implements Decorator {

  @DependedUpon
  public List<Metric> dependedUpon() {
    return Arrays.asList(MMMetrics.NCLOC_BY_NCLOC_DISTRIB, MMMetrics.NCLOC_BY_CC_DISTRIB);
  }

  @DependsUpon
  public List<Metric> dependsUpon() {
    return Arrays.asList(CoreMetrics.NCLOC, CoreMetrics.COMPLEXITY);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    if (Scopes.isBlockUnit(resource)) {
      buildMetricDistributionForMethod(context, CoreMetrics.NCLOC, MMMetrics.NCLOC_BY_NCLOC_DISTRIB, MMConfiguration.NCLOC_DISTRIBUTION_BOTTOM_LIMITS);
      buildMetricDistributionForMethod(context, CoreMetrics.COMPLEXITY, MMMetrics.NCLOC_BY_CC_DISTRIB, MMConfiguration.CC_DISTRIBUTION_BOTTOM_LIMITS);
      return;
    }

    computeAndSaveDistributionFromChildren(context, MMConfiguration.CC_DISTRIBUTION_BOTTOM_LIMITS, MMMetrics.NCLOC_BY_CC_DISTRIB);
    computeAndSaveDistributionFromChildren(context, MMConfiguration.NCLOC_DISTRIBUTION_BOTTOM_LIMITS, MMMetrics.NCLOC_BY_NCLOC_DISTRIB);
  }

  protected void computeAndSaveDistributionFromChildren(DecoratorContext context, Number[] bottomLimits, Metric metric) {
    Measure measure = computeDistributionFromChildren(context, bottomLimits, metric);
    if (MMPlugin.shouldPersistMeasures(context.getResource())) {
      context.saveMeasure(measure);
    }
    else {
      context.saveMeasure(measure.setPersistenceMode(PersistenceMode.MEMORY));
    }
  }

  protected Measure computeDistributionFromChildren(DecoratorContext context, Number[] bottomLimits, Metric metric) {
    RangeDistributionBuilder builder = new RangeDistributionBuilder(metric, bottomLimits);
    for (Measure childMeasure : context.getChildrenMeasures(metric)) {
      builder.add(childMeasure);
    }
    return builder.build();
  }

  private void buildMetricDistributionForMethod(DecoratorContext context, Metric metric, Metric resultMetric, Number[] bottomLimits) {
    int measure = MeasureUtils.getValue(context.getMeasure(metric), 0.0).intValue();
    int ncloc = MeasureUtils.getValue(context.getMeasure(CoreMetrics.NCLOC), 0.0).intValue();

    RangeDistributionBuilder distribution = new RangeDistributionBuilder(resultMetric, bottomLimits);
    distribution.add(measure, ncloc);
    context.saveMeasure(distribution.build());
  }

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }
}
