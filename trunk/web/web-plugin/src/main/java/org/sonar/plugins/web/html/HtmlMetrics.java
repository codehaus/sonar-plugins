/*
 * Copyright (C) 2010 Matthijs Galesloot
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

package org.sonar.plugins.web.html;


import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

/**
 * {@inheritDoc}
 */
public final class HtmlMetrics implements Metrics {

  public static final Metric W3C_MARKUP_VALIDITY = new Metric(
      "w3c_markup_validity",
      "W3C Markup Validity",
      "W3C Markup Validity",
      Metric.ValueType.PERCENT,
      Metric.DIRECTION_BETTER,
      true,
      CoreMetrics.DOMAIN_RULES
  );

  /**
   * {@inheritDoc}
   */
  public List<Metric> getMetrics() {
    return Arrays.asList(
        W3C_MARKUP_VALIDITY
    );
  }
}
