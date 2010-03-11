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
package org.sonar.plugins.taglist;


import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.*;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.api.rules.RulesManager;

import java.util.Arrays;
import java.util.List;

public class TaglistDistributionDecorator implements Decorator {

  private RulesManager rulesManager;
  private RulesProfile rulesProfile;

  public TaglistDistributionDecorator() {
    this.rulesManager = rulesManager;
    this.rulesProfile = rulesProfile;
  }

  @DependedUpon
  public List<Metric> generatesMetrics() {
    return Arrays.asList(TaglistMetrics.TAGS_DISTRIBUTION);
  }

  /**
   * {@inheritDoc}
   */
  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  public void decorate(Resource resource, DecoratorContext context) {

    // Calculate distribution on classes, but keep it in memory, not in DB
    if (ResourceUtils.isFile(resource)) {
      return;
    } else {
      // Otherwise, aggregate the distribution
      CountDistributionBuilder builder = new CountDistributionBuilder(TaglistMetrics.TAGS_DISTRIBUTION);

      // At modules and project levels, simply aggregate distribution of children
      for (Measure childMeasure : context.getChildrenMeasures(TaglistMetrics.TAGS_DISTRIBUTION)) {
        builder.add(childMeasure);
      }

      if (!builder.isEmpty()) {
        context.saveMeasure(builder.build());
      }
    }
  }
}