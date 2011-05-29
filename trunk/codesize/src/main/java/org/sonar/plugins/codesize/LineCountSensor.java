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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.Project;
import org.sonar.plugins.codesize.xml.SizingMetric;

/**
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class LineCountSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(LineCountSensor.class);
  private final SizingMetrics sizingMetrics;

  public LineCountSensor(SizingMetrics sizingMetrics) {
    this.sizingMetrics = sizingMetrics;
  }

  public void analyse(Project project, SensorContext sensorContext) {

    PropertiesBuilder<String, Long> counters = new PropertiesBuilder<String, Long>(SummaryMetrics.CODE_COUNTERS);

    for (SizingMetric sizingMetric : sizingMetrics.getSizingMetrics()) {

      LOG.debug("Calculating metric: " + sizingMetric.getName());

      LineCounter lineCounter = new LineCounter();
      lineCounter.setDefaultCharset(project.getFileSystem().getSourceCharset());
      long linesOfCode = lineCounter.calculateLinesOfCode(project.getFileSystem().getBasedir(), sizingMetric);

      counters.add(sizingMetric.getName(), linesOfCode);
    }

    sensorContext.saveMeasure(project, counters.build());
  }

  public boolean shouldExecuteOnProject(Project project) {
    // this sensor is executed on any type of project
    return true;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
