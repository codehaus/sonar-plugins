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
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;

public class TodoDecorator implements Decorator {

  public boolean shouldExecuteOnProject(Project project) {
    // only for Java projects
    return project.getFileSystem().hasJavaSourceFiles();
  }

  @DependedUpon
  public Metric generatesTodoMetric() {
    return SampleMetrics.TODO;
  }

  public void decorate(Resource resource, DecoratorContext context) {
    if (ResourceUtils.isProject(resource) && context.getMeasure(SampleMetrics.TODO) == null) {
      Double sum = MeasureUtils.sum(true, context.getChildrenMeasures(SampleMetrics.TODO));
      context.saveMeasure(SampleMetrics.TODO, sum);
    }
  }
}
