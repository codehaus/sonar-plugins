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

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sonar.api.measures.Metric;
import org.sonar.plugins.codesize.SizingMetrics;
import org.sonar.plugins.codesize.xml.SizingMetric;

public class LineCountMetricsTest {

  @Test
  public void testLineCountMetrics() {
    SizingMetrics lineCountMetrics = new SizingMetrics();

    int minExpectedMetrics = 5;
    assertTrue(minExpectedMetrics < lineCountMetrics.getSizingMetrics().size());
    assertTrue(minExpectedMetrics < SizingMetrics.getSizingProfile().getSizingMetrics().size());

    for (Metric metric : lineCountMetrics.getMetrics()) {
      assertNotNull(metric.getKey());
    }

    for (SizingMetric metric : SizingMetrics.getSizingProfile().getSizingMetrics()) {
      assertNotNull(metric.getKey());
      assertNotNull(metric.getName());
      assertNotNull(metric.getDescription());
      assertNotNull(metric.getSuffix());
      assertNotNull(metric.getSourceDir());
      assertNotNull(metric.getMetric());
    }
  }
}
