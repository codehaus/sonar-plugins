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

package org.sonar.plugins.webscanner.toetstool;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.util.StringUtils;
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
import org.sonar.plugins.webscanner.toetstool.xml.Guideline;
import org.sonar.plugins.webscanner.toetstool.xml.Guideline.ValidationType;
import org.sonar.plugins.webscanner.toetstool.xml.ToetstoolReport;

/**
 * @author Matthijs Galesloot
 * @since 0.1
 */
public final class ToetstoolSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(ToetstoolSensor.class);

  private final RulesProfile profile;
  private final RuleFinder ruleFinder;
  private final MavenSession session;

  public ToetstoolSensor(MavenSession session, RulesProfile profile, RuleFinder ruleFinder) {
    LOG.info("Profile: " + profile.getName());
    this.session = session;
    this.profile = profile;
    this.ruleFinder = ruleFinder;
  }

  private void addViolation(SensorContext sensorContext, org.sonar.api.resources.File resource, Guideline guideline, boolean error) {
    String ruleKey = guideline.getRef();
    Rule rule = ruleFinder.findByKey(ToetstoolRuleRepository.REPOSITORY_KEY, ruleKey);
    String remark = StringEscapeUtils.unescapeXml(guideline.getRemark());
    // second unescape needed?
    remark = StringEscapeUtils.unescapeHtml(remark);
    if (rule != null) {
      Violation violation = Violation.create(rule, resource);
      violation.setMessage((error ? "" : "Warning: ") + remark);
      sensorContext.saveViolation(violation);
      LOG.debug(resource.getName() + ": " + guideline.getRef() + ":" + remark);
    } else {
      LOG.warn("Could not find Toetstool Rule " + guideline.getRef() + ", Message = " + remark);
    }
  }

  /**
   * Find Toetstool Validation reports in the source tree and save violations to Sonar. The Toetstool reports have file extension .ttr.
   */
  public void analyse(Project project, SensorContext sensorContext) {

    prepareScanning(project);

    List<File> files = project.getFileSystem().getSourceFiles(new Html(project));

    // create validator
    ToetsToolValidator validator = new ToetsToolValidator((String) project.getProperty("sonar.toetstool.url"), project.getFileSystem()
        .getBasedir() + "/" + (String) project.getProperty("sonar.toetstool.cssDir"));

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

  private boolean hasToetstoolRules() {
    for (ActiveRule activeRule : profile.getActiveRules()) {
      if (ToetstoolRuleRepository.REPOSITORY_KEY.equals(activeRule.getRepositoryKey())) {
        return true;
      }
    }
    return false;
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

    ToetstoolReport report = ToetstoolReport.fromXml(reportFile);

    // save errors
    for (Guideline guideline : report.getReport().getGuidelines()) {
      if (guideline.getType() == ValidationType.error) {
        addViolation(sensorContext, htmlFile, guideline, true);
      }
    }
    // save warnings
    for (Guideline guideline : report.getReport().getGuidelines()) {
      if (guideline.getType() == ValidationType.warning) {
        addViolation(sensorContext, htmlFile, guideline, false);
      }
    }

    return report.getReport().getCounters().getError() == 0;
  }

  /**
   * scan all files and find corresponding report file, created by the validator.
   */
  private void saveResults(Project project, SensorContext sensorContext, ToetsToolValidator validator, List<File> files) {

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
    sensorContext.saveMeasure(HtmlMetrics.TOETSTOOL_VALIDITY, percentageValid);
  }

  /**
   * This sensor only executes on Html projects with Toetstool rules.
   */
  public boolean shouldExecuteOnProject(Project project) {
    return StringUtils.equals(Html.KEY, project.getLanguageKey()) && hasToetstoolRules();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
