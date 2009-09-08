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
package org.sonar.plugins.ral.batch;

import org.apache.commons.lang.math.RandomUtils;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import java.util.List;
import java.util.Arrays;

public class RandomMeasuresDecorator implements Decorator {

  @DependedUpon
  public List<Metric> generatesMetric() {
    return Arrays.asList(SampleMetrics.RANDOM_LEVEL, SampleMetrics.RANDOM_BOOLEAN);
  }

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  public void decorate(Resource resource, DecoratorContext context) {
    Metric.Level[] levels = Metric.Level.values();
    int index = RandomUtils.nextInt(levels.length);
    context.saveMeasure(new Measure(SampleMetrics.RANDOM_LEVEL, levels[index].toString()));

    boolean b = RandomUtils.nextBoolean();
    context.saveMeasure(SampleMetrics.RANDOM_BOOLEAN, b ? 1.0 : 0.0);
  }
}
