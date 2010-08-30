/*
 * Copyright (C) 2010 The Original Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonar.plugins.codesize;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.jfree.util.Log;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.plugins.codesize.xml.SizingMetric;

/**
 * {@inheritDoc}
 */
public final class CodeSizeDecorator implements Decorator {

  private final Configuration configuration;

  /**
   * {@inheritDoc}
   */
  public CodeSizeDecorator(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * {@inheritDoc}
   */
  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  @DependsUpon
  public List<Metric> dependsOnMetrics() {
    return new SizingMetrics().getSizingMetrics();
  }

  @DependedUpon
  public List<Metric> generatesMetrics() {
    return Arrays.asList(SummaryMetrics.CODE_DISTRIBUTION);
  }

  /**
   * {@inheritDoc}
   */
  public void decorate(Resource resource, DecoratorContext context) {

    // aggregate results from children
    for (SizingMetric sizingMetric : SizingMetrics.getSizingProfile().getSizingMetrics()) {
      aggregate(sizingMetric.getMetric(), context);
    }

    double totalLines = 0;

    PropertiesBuilder<String, Double> distribution = new PropertiesBuilder<String, Double>(SummaryMetrics.CODE_DISTRIBUTION);

    // Calculate the total lines of code.
    for (SizingMetric metric : SizingMetrics.getSizingProfile().getSizingMetrics()) {
      Measure measure = context.getMeasure(metric.getMetric());
      if (measure != null) {
        totalLines += measure.getValue();
      }
    }

    // Next calculate the % of each measure
    for (SizingMetric metric : SizingMetrics.getSizingProfile().getSizingMetrics()) {
      Measure measure = context.getMeasure(metric.getMetric());
      if (measure != null) {
        double percentage = measure.getValue() / totalLines * 100;
        if (percentage > 0.5) {
          percentage = Math.floor(percentage * 100.0) / 100;
          distribution.add(metric.getName(), percentage);
        }
      }
    }

    context.saveMeasure(distribution.build());
    context.saveMeasure(SummaryMetrics.TOTAL_LINES, totalLines);
  }

  private void aggregate(Metric metric, DecoratorContext context) {
    double value = 0;
    for (Measure m : context.getChildrenMeasures(metric)) {
      value += m.getValue();
    }

    Log.debug("Saving LOC for " + context.getResource().getName() + ":" + value);
    Measure measure = context.getMeasure(metric);
    if (measure == null) {
      context.saveMeasure(metric, value);
    } else {
      measure.setValue(measure.getValue() + value);
    }
  }

}
