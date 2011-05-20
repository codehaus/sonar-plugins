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

package org.sonar.plugins.webscanner.scanner;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Logger;
import org.sonar.api.resources.InputFile;

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

  public void validateFiles(List<InputFile> inputfiles) {

    int n = 0;
    for (InputFile inputfile : inputfiles) {
      // skip analysis if the report already exists
      File reportFile = visitor.reportFile(inputfile.getFile());
      if (!reportFile.exists()) {
        if (n++ > 0) {
          visitor.waitBetweenValidationRequests();
        }
        LOG.debug("Validating " + inputfile.getRelativePath() + "...");
        visitor.validateFile(inputfile.getFile());
      }
    }
  }

  public static Collection<File> getReportFiles(File htmlFolder, final String reportXml) {
    Collection<File> reportFiles = FileUtils.listFiles(htmlFolder, new IOFileFilter() {

      public boolean accept(File file) {
        return file.getName().endsWith(reportXml);
      }

      public boolean accept(File dir, String name) {
        return name.endsWith(reportXml);
      }
    }, new IOFileFilter() {

      public boolean accept(File file) {
        return true;
      }

      public boolean accept(File dir, String name) {
        return true;
      }
    });

    return reportFiles;
  }
}
