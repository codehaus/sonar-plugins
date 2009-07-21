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
package org.sonar.plugins.emma;

import org.sonar.api.Plugins;
import org.sonar.api.batch.AbstractCoverageExtension;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;

public class ProjectCoverageDecorator extends AbstractCoverageExtension implements Decorator {

  public ProjectCoverageDecorator(Plugins plugins) {
    super(plugins);
  }

  @DependedUpon
  public Metric generatesMetric() {
    return CoreMetrics.COVERAGE;
  }


  public boolean shouldExecuteOnProject(Project project) {
    return shouldExecuteCoveragePluginOnProject(project);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    if (ResourceUtils.isFile(resource) || context.getMeasure(CoreMetrics.COVERAGE) != null) {
      return;
    }

    double elements = 0.0;
    double total = 0.0;

    for (DecoratorContext child : context.getChildren()) {
      Measure statements = child.getMeasure(CoreMetrics.STATEMENTS);
      Measure functions = child.getMeasure(CoreMetrics.FUNCTIONS);
      Measure accessors = child.getMeasure(CoreMetrics.ACCESSORS);
      Measure coverage = child.getMeasure(CoreMetrics.COVERAGE);

      double childElements = MeasureUtils.sum(true, statements, functions, accessors);
      if (childElements > 0 && MeasureUtils.hasValue(coverage)) {
        double childCoverage = coverage.getValue();
        elements += childElements;
        total += (childElements * childCoverage);
      }
    }
    if (elements == 0d) {
      context.saveMeasure(CoreMetrics.COVERAGE, 0d);
    } else {
      context.saveMeasure(CoreMetrics.COVERAGE, total / elements);
    }
  }
}
