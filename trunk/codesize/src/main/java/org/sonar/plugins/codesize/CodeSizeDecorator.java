/*
 * Codesize
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.util.Log;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.api.utils.KeyValueFormat;

/**
 * {@inheritDoc}
 */
public final class CodeSizeDecorator implements Decorator {

  /**
   * {@inheritDoc}
   */
  public void decorate(Resource resource, DecoratorContext context) {

    if (ResourceUtils.isProject(resource)) {

      Log.debug("Saving LOC for " + context.getResource().getName());

      Collection<Measure> childMeasures = context.getChildrenMeasures(SummaryMetrics.CODE_COUNTERS);

      if (!childMeasures.isEmpty()) {
        Measure projectMeasure = context.getMeasure(SummaryMetrics.CODE_COUNTERS);
        final PropertiesBuilder<String, Integer> counters = new PropertiesBuilder<String, Integer>(SummaryMetrics.CODE_COUNTERS);
        if (projectMeasure != null) {
          Map<String, Integer> map = KeyValueFormat.parseStringInt(projectMeasure.getData());
          counters.addAll(map);
        }

        for (Measure m : childMeasures) {
          String data = m.getData();
          Map<String, Integer> childcounters = KeyValueFormat.parseStringInt(data);
          for (Entry<String, Integer> entry : childcounters.entrySet()) {
            Integer value = counters.getProps().get(entry.getKey());
            counters.add(entry.getKey(), value + entry.getValue());
          }
        }

        if (projectMeasure != null) {
          projectMeasure.setData(counters.buildData());
        } else {
          context.saveMeasure(counters.build());
        }
      }
    }
  }

  @DependedUpon
  public List<Metric> generatesMetrics() {
    return Arrays.asList(SummaryMetrics.CODE_COUNTERS);
  }

  /**
   * {@inheritDoc}
   */
  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }
}
