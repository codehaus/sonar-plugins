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

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.sonar.api.resources.Project;

public class LineCountMetricsTest {

  @Test
  public void testLineCountMetrics() {
    Project project = new Project("test");
    project.setConfiguration(new PropertiesConfiguration());

    SizingProfile lineCountMetrics = new SizingProfile(project.getConfiguration());

    int minExpectedMetrics = 5;
    assertTrue(minExpectedMetrics < lineCountMetrics.getSizingMetrics().size());
    assertTrue(minExpectedMetrics < lineCountMetrics.getSizingMetrics().size());

    for (SizingMetric metric : lineCountMetrics.getSizingMetrics()) {
      assertNotNull(metric.getName());
      assertNotNull(metric.getIncludes());
    }
  }
}
