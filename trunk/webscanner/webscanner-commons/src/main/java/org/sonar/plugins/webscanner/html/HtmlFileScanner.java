/*
 * Sonar Webscanner Plugin
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

package org.sonar.plugins.webscanner.api.html;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.sonar.plugins.webscanner.api.html.FileSet.HtmlFile;

/**
 * Scanner for html files.
 *
 * @author Matthijs Galesloot
 * @since 0.1
 *
 */
public final class HtmlFileScanner {

  private static final Logger LOG = Logger.getLogger(HtmlFileScanner.class);

  private final HtmlFileVisitor visitor;

  public HtmlFileScanner(HtmlFileVisitor visitor) {
    this.visitor = visitor;
  }

  public void prepareHtml(File folder) {

    if (folder.exists()) {

      HtmlScanner htmlScanner = new HtmlScanner();
      htmlScanner.prepare(folder);
    }
  }

  protected List<HtmlFile> randomSubset(List<HtmlFile> htmlFiles, Integer amount) {
    List<HtmlFile> newCollection = new ArrayList<HtmlFile>();
    for (int i = 0; i < amount && i < htmlFiles.size(); i++) {
      newCollection.add(htmlFiles.get(i));
    }
    return newCollection;
  }

  protected List<File> randomSubsetFiles(List<File> files, Integer amount) {
    List<File> newCollection = new ArrayList<File>();
    for (int i = 0; i < amount && i < files.size(); i++) {
      newCollection.add(files.get(i));
    }
    return newCollection;
  }

  private List<HtmlFile> removeDuplicates(List<HtmlFile> list) {
    final List<HtmlFile> files = new ArrayList<HtmlFile>();
    for (HtmlFile file : list) {
      if (file.duplicateFile == null) {
        files.add(file);
      }
    }
    return files;
  }

  /**
   * Validate a set of files using the service.
   */
  public void validateFiles(File folder, Integer nrOfSamples) {
    prepareHtml(folder);

    FileSet fileSet = FileSet.fromXml(FileSet.getPath(folder));
    List<HtmlFile> files = removeDuplicates(fileSet.files);

    if (nrOfSamples != null && nrOfSamples > 0) {
      files = randomSubset(files, nrOfSamples);
    }

    int n = 0;
    for (HtmlFile htmlFile : files) {
      if (n++ > 0) {
        visitor.waitBetweenValidationRequests();
      }

      File file = new File(folder.getPath() + "/" + htmlFile.path);

      // skip analysis if the report already exists
      File reportFile = visitor.reportFile(file);
      if (!reportFile.exists() || FileUtils.isFileNewer(file, reportFile)) {
        visitor.validateFile(file);
      }
    }
  }

  public void validateFiles(List<File> files, Integer nrOfSamples) {

    if (nrOfSamples != null && nrOfSamples > 0) {
      files = randomSubsetFiles(files, nrOfSamples);
    }

    int n = 0;
    for (File file : files) {
      // skip analysis if the report already exists
      File reportFile = visitor.reportFile(file);
      if (!reportFile.exists()) {
        if (n++ > 0) {
          visitor.waitBetweenValidationRequests();
        }
        LOG.debug("Validating " + file.getPath() + "...");
        visitor.validateFile(file);
      }
    }
  }
}
