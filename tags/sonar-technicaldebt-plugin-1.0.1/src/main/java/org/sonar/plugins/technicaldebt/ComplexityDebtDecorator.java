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

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.CountDistributionBuilder;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ComplexityDebtDecorator implements Decorator {

  public List<Metric> dependedUpon() {
    return Arrays.asList(TechnicalDebtMetrics.TECHNICAL_DEBT_COMPLEXITY);
  }

  public void decorate(Resource resource, DecoratorContext context) {
    Collection<Measure> measures = context.getChildrenMeasures(TechnicalDebtMetrics.TECHNICAL_DEBT_COMPLEXITY);
    if (measures == null || measures.isEmpty()) {
      return;
    } else {
      CountDistributionBuilder distribution = new CountDistributionBuilder(TechnicalDebtMetrics.TECHNICAL_DEBT_COMPLEXITY);
      for (Measure measure : measures) {
        distribution.add(measure);
      }
      context.saveMeasure(distribution.build());
    }
  }

  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }
}
