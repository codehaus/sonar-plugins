/*
 * Sonar Codesize Plugin
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

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.api.utils.KeyValueFormat;

/**
 * Decorator to aggregate CODE_COUNTERS metric from subprojects.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class CodeSizeDecorator extends CodeSizeBatchExtension implements Decorator {

  /**
   * {@inheritDoc}
   */
  public void decorate(Resource resource, DecoratorContext context) {

    if (ResourceUtils.isProject(resource)) {

      Collection<Measure> childMeasures = context.getChildrenMeasures(CodesizeMetrics.CODE_COUNTERS);

      if (!childMeasures.isEmpty()) {
        Measure projectMeasure = context.getMeasure(CodesizeMetrics.CODE_COUNTERS);
        final PropertiesBuilder<String, Integer> counters = new PropertiesBuilder<String, Integer>(CodesizeMetrics.CODE_COUNTERS);
        if (projectMeasure != null) {
          Map<String, Integer> map = KeyValueFormat.parseStringInt(projectMeasure.getData());
          counters.addAll(map);
        }

        for (Measure childMeasure : childMeasures) {
          Map<String, Integer> childcounters = KeyValueFormat.parseStringInt(childMeasure.getData());
          for (Entry<String, Integer> entry : childcounters.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (counters.getProps().containsKey(key)) {
              counters.add(key, value + counters.getProps().get(key));
            } else {
              counters.add(key, value);
            }
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
}
