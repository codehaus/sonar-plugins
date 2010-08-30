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

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

public final class SummaryMetrics implements Metrics {

  public static final Metric CODE_DISTRIBUTION = new Metric("code_distribution", "Code Distribution", "Code Distribution",
      Metric.ValueType.DISTRIB, Metric.DIRECTION_NONE, false, CoreMetrics.DOMAIN_GENERAL);

  public static final Metric TOTAL_LINES = new Metric("total_lines", "Total Lines", "Total Lines", Metric.ValueType.INT,
      Metric.DIRECTION_NONE, false, CoreMetrics.DOMAIN_SIZE);

  public List<Metric> getMetrics() {
    return Arrays.asList(CODE_DISTRIBUTION, TOTAL_LINES);
  }
}