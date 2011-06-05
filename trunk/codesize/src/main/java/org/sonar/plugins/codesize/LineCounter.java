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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.codehaus.plexus.util.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Count lines of code in a file.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 *
 */
class LineCounter {

  private static final Logger LOG = LoggerFactory.getLogger(LineCounter.class);

  private Charset defaultCharset = Charset.defaultCharset();

  public int calculateLinesOfCode(File baseDir, FileSetDefinition fileSetDefinition) {
    List<File> files = getFiles(baseDir, fileSetDefinition.getIncludes(), fileSetDefinition.getExcludes());

    LOG.debug("Found " + files.size() + " files");
    int lines = 0;
    for (File file : files) {
      lines += countFile(file);
    }

    return lines;
  }

  private int countFile(File file) {

    int lines = 0;
    LineIterator lineIterator = null;

    try {
      lineIterator = FileUtils.lineIterator(file, defaultCharset.name());

      for (; lineIterator.hasNext(); lineIterator.next()) {
        lines++;
      }
    } catch (IOException e) {
      LOG.error("Could not open file", e);
    } finally {
      LineIterator.closeQuietly(lineIterator);
    }
    LOG.debug(file.getName() + ": " + lines);
    return lines;
  }

  private List<File> getFiles(File directory, List<String> includes, List<String> excludes) {
    List<File> fileList = new ArrayList<File>();

    // set up scanner
    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setBasedir(directory);
    scanner.setIncludes(includes.toArray(new String[includes.size()]));
    scanner.setExcludes(excludes.toArray(new String[excludes.size()]));
    scanner.scan();

    // start scan
    for (String fileName : scanner.getIncludedFiles()) {
      fileList.add(new File(directory, fileName));
    }
    return fileList;
  }

  public void setDefaultCharset(Charset defaultCharset) {
    this.defaultCharset = defaultCharset;
  }
}
