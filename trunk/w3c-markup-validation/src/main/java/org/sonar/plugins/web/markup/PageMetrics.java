/*
 * Sonar W3C Markup Validation Plugin
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
package org.sonar.plugins.web.markup;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

/**
 * Metrics for pages.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class PageMetrics implements Metrics {

  public static final Metric PAGE = new Metric.Builder("page_metrics", "Page Metrics", Metric.ValueType.DATA).setDomain(
      CoreMetrics.DOMAIN_GENERAL).create();

  public List<Metric> getMetrics() {
    return Arrays.asList(PAGE);
  }
}