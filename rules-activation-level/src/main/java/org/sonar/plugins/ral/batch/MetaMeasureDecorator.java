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

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import java.util.Arrays;
import java.util.List;

public class MetaMeasureDecorator implements Decorator {

  @DependedUpon
  public Metric generatesMetrics() {
    return SampleMetrics.META;
  }

  @DependsUpon
  public List<Metric> dependsUponMetrics() {
    return Arrays.asList(CoreMetrics.COVERAGE, CoreMetrics.VIOLATIONS_DENSITY);
  }

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  protected double compute(Measure m1, Measure m2) {
    // just a silly formula
    return (m1.getValue() + m2.getValue()) / 2.0;
  }

  public void decorate(Resource resource, DecoratorContext context) {
    Measure coverage = context.getMeasure(CoreMetrics.COVERAGE);
    Measure violationsDensity = context.getMeasure(CoreMetrics.VIOLATIONS_DENSITY);

    if (MeasureUtils.hasValue(violationsDensity) && MeasureUtils.hasValue(coverage)) {
      context.saveMeasure(SampleMetrics.META, compute(violationsDensity, coverage));
    }
  }
}
