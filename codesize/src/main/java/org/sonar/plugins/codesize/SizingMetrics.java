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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.codesize.xml.SizingMetric;
import org.sonar.plugins.codesize.xml.SizingProfile;

import com.thoughtworks.xstream.XStream;

public final class SizingMetrics implements Metrics {

  private static final Logger LOG = LoggerFactory.getLogger(SizingMetrics.class);

  private static SizingProfile SIZING_METRICS;

  private static String getConfigurationFromFile() {
    InputStream inputStream = SizingMetrics.class.getResourceAsStream("/metrics.xml");
    try {
      return IOUtils.toString(inputStream, "UTF-8");
    } catch (IOException e) {
      throw new SonarException("Configuration file not found: metrics.xml", e);
    } finally {
      IOUtils.closeQuietly(inputStream);
    }
  }

  public static SizingProfile getSizingProfile() {
    return SIZING_METRICS;
  }

  private static XStream getXStream() {
    XStream xstream = new XStream();
    xstream.setClassLoader(SizingMetrics.class.getClassLoader());
    xstream.processAnnotations(SizingProfile.class);
    xstream.processAnnotations(SizingMetric.class);

    return xstream;
  }

  private final List<Metric> metrics;

  public SizingMetrics() {

    metrics = new ArrayList<Metric>();
    SIZING_METRICS = (SizingProfile) getXStream().fromXML(getConfigurationFromFile());
    for (SizingMetric sizingMetric : SIZING_METRICS.getSizingMetrics()) {
      LOG.debug("Load sizing metric : " + sizingMetric.getKey());

      if ("generated_lines".equals(sizingMetric.getKey())) {
        sizingMetric.setMetric(CoreMetrics.GENERATED_LINES);
      } else {
        Metric metric = new Metric(sizingMetric.getKey(), sizingMetric.getName(), sizingMetric.getDescription(), Metric.ValueType.INT,
            Metric.DIRECTION_NONE, false, CoreMetrics.DOMAIN_SIZE);
        sizingMetric.setMetric(metric);
        metrics.add(metric);
      }
    }
  }

  public List<Metric> getMetrics() {
    return metrics;
  }

  public List<Metric> getSizingMetrics() {
    return metrics;
  }
}