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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sonar.commons.Metric;
import org.sonar.commons.resources.Measure;
import org.sonar.commons.resources.Resource;
import org.sonar.plugins.api.jobs.Job;
import org.sonar.plugins.api.jobs.JobContext;
import org.sonar.plugins.api.measures.CountDistributionBuilder;

public class TaglistDistributionSumJob implements Job {

  public List<Metric> dependsOnMetrics() {
    return Collections.emptyList();
  }

  public List<Class<? extends Job>> dependsOnJobs() {
    return Collections.emptyList();
  }

  public List<Metric> generatesMetrics() {
    return Arrays.asList(TaglistMetrics.TAGS_DISTRIBUTION);
  }

  public boolean shouldExecuteOnProject(Resource project) {
    return true;
  }

  public boolean shouldExecuteOnResource(Resource resource) {
    return !resource.isFile();
  }

  public void execute(JobContext context) {

    Measure measure = context.getMeasure(TaglistMetrics.TAGS_DISTRIBUTION);
    if (measure != null) {
      // already calculated
      return;
    }
    CountDistributionBuilder builder = new CountDistributionBuilder(TaglistMetrics.TAGS_DISTRIBUTION);
    
    builder.clear();
    for (Measure childMeasure : context.getChildrenMeasures(TaglistMetrics.TAGS_DISTRIBUTION)) {
      builder.addDistributionMeasure(childMeasure);
    }

    if (!builder.isEmpty()) {
      context.addMeasure(builder.build());
    }
  }
}