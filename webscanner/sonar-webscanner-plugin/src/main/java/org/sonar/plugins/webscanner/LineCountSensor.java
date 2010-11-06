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

package org.sonar.plugins.webscanner;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Project;
import org.sonar.plugins.webscanner.language.Html;
import org.sonar.plugins.webscanner.language.HtmlFile;

/**
 * @author Matthijs Galesloot
 * @since 0.1
 */
public final class LineCountSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(LineCountSensor.class);

  public LineCountSensor() {

  }

  public void analyse(Project project, SensorContext sensorContext) {

    ProjectConfiguration.configureSourceDir(project);

    for (File file : project.getFileSystem().getSourceFiles(new Html(project))) {
      HtmlFile htmlFile = HtmlFile.fromIOFile(file, project.getFileSystem().getSourceDirs());
      addMeasures(sensorContext, file, htmlFile);
    }
  }

  private void addMeasures(SensorContext sensorContext, File file, HtmlFile htmlFile) {

    LineIterator iterator = null;
    int numLines = 0;
    int numEmptyLines = 0;

    try {
      iterator = FileUtils.lineIterator(file);

      while (iterator.hasNext()) {
        String line = iterator.nextLine();
        numLines++;
        if (StringUtils.isEmpty(line)) {
          numEmptyLines++;
        }
      }
    } catch (IOException e) {
      LOG.warn(e.getMessage());
    } finally {
      LineIterator.closeQuietly(iterator);
    }

    sensorContext.saveMeasure(htmlFile, CoreMetrics.LINES, (double) numLines);
    sensorContext.saveMeasure(htmlFile, CoreMetrics.NCLOC, (double) numLines - numEmptyLines);

    LOG.debug("LineCountSensor: " + htmlFile.getKey() + ":" + numLines + "," + numEmptyLines + "," + 0);
  }

  /**
   * This sensor only executes on Web projects with W3C Markup rules.
   */
  public boolean shouldExecuteOnProject(Project project) {
    return isEnabled(project) && Html.KEY.equals(project.getLanguage().getKey());
  }

  private boolean isEnabled(Project project) {
    return project.getConfiguration().getBoolean(CoreProperties.CORE_IMPORT_SOURCES_PROPERTY,
        CoreProperties.CORE_IMPORT_SOURCES_DEFAULT_VALUE);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
