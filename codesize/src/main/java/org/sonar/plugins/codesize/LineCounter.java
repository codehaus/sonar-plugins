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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.plugins.codesize.xml.SizingMetric;

class LineCounter {

  private static final Logger LOG = LoggerFactory.getLogger(LineCounter.class);

  private int lines;
  private int files;

  private final Project project;

  public LineCounter(Project project) {
    this.project = project;
  }

  public int getFiles() {
    return files;
  }

  private void addFile(File file) {

    try {
      LineIterator lineIterator = FileUtils.lineIterator(file, project.getFileSystem().getSourceCharset().name());
      while (lineIterator.hasNext()) {
        String line = lineIterator.nextLine();
        if (!StringUtils.isEmpty(line)) {
          lines++;
        }
      }
      lineIterator.close();
      files++;

      LOG.debug(file.getName() + "(" + lines + "," + lines + ")");
    } catch (IOException e) {
      LOG.warn("Could not open file", e);
    }
  }

  public void calculateLinesOfCode(SensorContext sensorContext, SizingMetric sizingMetric) {
    File sourceDir = project.getFileSystem().resolvePath(sizingMetric.getSourceDir());
    List<File> files = getFiles(Arrays.asList(sourceDir), new String[] {sizingMetric.getSuffix()} );

    LOG.debug("Found " + files.size() + " files");
    for (File file : files) {
      addFile(file);
    }

    sensorContext.saveMeasure(project, sizingMetric.getMetric(), (double) lines);
  }

  @SuppressWarnings("unchecked")
  private List<File> getFiles(List<File> directories, String[] extensions) {
    List<File> result = new ArrayList<File>();

    for (File dir : directories) {
      if (dir.exists()) {
        result.addAll(FileUtils.listFiles(dir, extensions, true));
      }
    }
    return result;
  }

}
