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

package org.sonar.plugins.webscanner.markup;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.maven.execution.MavenSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.webscanner.HtmlMetrics;
import org.sonar.plugins.webscanner.html.HtmlFileScanner;
import org.sonar.plugins.webscanner.html.HtmlScanner;
import org.sonar.plugins.webscanner.language.Html;
import org.sonar.plugins.webscanner.language.ProjectConfiguration;
import org.sonar.plugins.webscanner.markup.rules.MarkupRuleRepository;

/**
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class W3CMarkupSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(W3CMarkupSensor.class);

  public static final String VALIDATION_URL = "sonar.w3cmarkup.url";
  private final RulesProfile profile;
  private final RuleFinder ruleFinder;

  private final MavenSession session;

  public W3CMarkupSensor(MavenSession session, RulesProfile profile, RuleFinder ruleFinder) {
    LOG.info("Profile: " + profile.getName());
    this.session = session;
    this.profile = profile;
    this.ruleFinder = ruleFinder;
  }

  private void addViolation(SensorContext sensorContext, org.sonar.api.resources.File resource, MarkupMessage message, boolean error) {
    String ruleKey = makeIdentifier(message.getMessageId());
    Rule rule = ruleFinder.findByKey(MarkupRuleRepository.REPOSITORY_KEY, ruleKey);
    if (rule != null) {
      Violation violation = Violation.create(rule, resource).setLineId(message.getLine());
      violation.setMessage((error ? "" : "Warning: ") + message.getMessage());
      sensorContext.saveViolation(violation);
      LOG.debug(resource.getName() + ": " + message.getMessageId() + ":" + message.getMessage());
    } else {
      LOG.warn("Could not find Markup Rule " + message.getMessageId() + ", Message = " + message.getMessage());
    }
  }

  /**
   * Find W3C Validation Markup reports in the source tree and save violations to Sonar. The Markup reports have file extension .mur.
   */
  public void analyse(Project project, SensorContext sensorContext) {

    prepareScanning(project);

    List<File> files = project.getFileSystem().getSourceFiles(new Html(project));

    // create validator
    MarkupValidator validator = new MarkupValidator((String) project.getProperty(VALIDATION_URL));

    // configure proxy
    if (session.getSettings().getActiveProxy() != null) {
      validator.setProxyHost(session.getSettings().getActiveProxy().getHost());
      validator.setProxyPort(session.getSettings().getActiveProxy().getPort());
    }

    // start the html scanner
    HtmlFileScanner htmlFileScanner = new HtmlFileScanner(validator);
    htmlFileScanner.validateFiles(files, ProjectConfiguration.getNrOfSamples(project));

    // save analysis to sonar
    saveResults(project, sensorContext, validator, files);
  }

  private boolean hasMarkupRules() {
    for (ActiveRule activeRule : profile.getActiveRules()) {
      if (MarkupRuleRepository.REPOSITORY_KEY.equals(activeRule.getRepositoryKey())) {
        return true;
      }
    }
    return false;
  }

  private String makeIdentifier(String idString) {
    int id = NumberUtils.toInt(idString, -1);
    if (id >= 0) {
      return String.format("%03d", id);
    } else {
      return idString;
    }
  }

  private void prepareScanning(Project project) {

    ProjectConfiguration.configureSourceDir(project);

    for (File sourceDir : project.getFileSystem().getSourceDirs()) {
      if ( !sourceDir.exists()) {
        LOG.error("Missing HTML directory: " + sourceDir.getPath());
        continue;
      }

      LOG.info("HTML Dir:" + sourceDir);

      HtmlScanner htmlScanner = new HtmlScanner();
      htmlScanner.prepare(sourceDir);
    }
  }

  private boolean readValidationReport(SensorContext sensorContext, File reportFile, org.sonar.api.resources.File htmlFile) {

    MarkupReport report = MarkupReport.fromXml(reportFile);

    // save errors
    for (MarkupMessage error : report.getErrors()) {
      addViolation(sensorContext, htmlFile, error, true);
    }

    // save warnings
    for (MarkupMessage warning : report.getWarnings()) {
      addViolation(sensorContext, htmlFile, warning, false);
    }

    return report.isValid();
  }

  private void saveResults(Project project, SensorContext sensorContext, MarkupValidator validator, List<File> files) {
    int numValid = 0;
    int numFiles = 0;

    for (File file : files) {
      org.sonar.api.resources.File htmlFile = org.sonar.api.resources.File.fromIOFile(file, project.getFileSystem().getSourceDirs());
      File reportFile = validator.reportFile(file);

      if (reportFile.exists()) {
        boolean isValid = readValidationReport(sensorContext, reportFile, htmlFile);
        if (isValid) {
          numValid++;
        }
      } else {
        LOG.warn("Missing reportfile for file " + file.getPath());
      }
      numFiles++;
    }

    double percentageValid = numFiles > 0 ? (double) (numFiles - numValid) / numFiles : 100;
    sensorContext.saveMeasure(HtmlMetrics.W3C_MARKUP_VALIDITY, percentageValid);
  }

  /**
   * This sensor only executes on Html projects with W3C Markup rules.
   */
  public boolean shouldExecuteOnProject(Project project) {
    return Html.KEY.equals(project.getLanguageKey()) && hasMarkupRules();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
