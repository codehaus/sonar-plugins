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

package org.sonar.plugins.web;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.sonar.api.batch.GeneratesViolations;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.language.WebRecognizer;
import org.sonar.plugins.web.lex.PageLexer;
import org.sonar.plugins.web.rules.AbstractPageCheck;
import org.sonar.plugins.web.rules.PageChecks;
import org.sonar.plugins.web.visitor.PageCountLines;
import org.sonar.plugins.web.visitor.PageScanner;
import org.sonar.plugins.web.visitor.WebDependencyDetector;
import org.sonar.squid.measures.Metric;
import org.sonar.squid.text.Source;

/**
 * @author Matthijs Galesloot
 */
public final class WebSensor implements Sensor, GeneratesViolations {

  private RulesProfile profile;

  public WebSensor(RulesProfile profile) {
    this.profile = profile;
  }

  /**
   * This sensor executes on Web projects.
   */
  public boolean shouldExecuteOnProject(Project project) {
    return Web.INSTANCE.equals(project.getLanguage());
  }

  public void analyse(Project project, SensorContext sensorContext) {

    // configure the lexer
    PageLexer lexer = new PageLexer();

    // configure scanner
    PageScanner scanner = new PageScanner();
    for (AbstractPageCheck check : PageChecks.getChecks(profile)) {
      scanner.addVisitor(check);
    }
    scanner.addVisitor(new PageCountLines());
    scanner.addVisitor(new WebDependencyDetector(project.getFileSystem()));

    for (File webFile : project.getFileSystem().getSourceFiles(Web.INSTANCE)) {

      try {
        WebFile resource = WebFile.fromIOFile(webFile, project.getFileSystem().getSourceDirs());

        switch (resource.getFileType()) {
          case JavaScript:
          case Html:
          case Css:
            analyzeClientScript(webFile, resource, project.getFileSystem(), sensorContext);
            break;
          default:
            scanner.scan(lexer.parse(new FileReader(webFile)), sensorContext, resource);
            break;
        }
      } catch (Exception e) {
        WebUtils.LOG.error("Could not analyze the file " + webFile.getAbsolutePath(), e);
      }
    }
  }

  private void analyzeClientScript(File webFile, WebFile webResource, ProjectFileSystem projectFileSystem, SensorContext sensorContext)
      throws IOException {
    Reader reader = null;
    try {
      reader = new StringReader(FileUtils.readFileToString(webFile, projectFileSystem.getSourceCharset().name()));
      Source source = new Source(reader, new WebRecognizer(), "");
      double linesOfCode = source.getMeasure(Metric.LINES);
      sensorContext.saveMeasure(webResource, CoreMetrics.LINES, linesOfCode);
      sensorContext.saveMeasure(webResource, CoreMetrics.NCLOC, linesOfCode);

    } finally {
      IOUtils.closeQuietly(reader);
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
