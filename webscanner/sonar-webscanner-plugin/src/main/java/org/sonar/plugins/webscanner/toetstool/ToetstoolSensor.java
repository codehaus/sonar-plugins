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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.webscanner.HtmlProjectFileSystem;
import org.sonar.plugins.webscanner.WebScannerPlugin;
import org.sonar.plugins.webscanner.language.Html;
import org.sonar.plugins.webscanner.language.HtmlMetrics;
import org.sonar.plugins.webscanner.scanner.HtmlFileScanner;
import org.sonar.plugins.webscanner.toetstool.rules.ToetstoolRuleRepository;
import org.sonar.plugins.webscanner.toetstool.validation.ToetsToolValidator;
import org.sonar.plugins.webscanner.toetstool.xml.Guideline;
import org.sonar.plugins.webscanner.toetstool.xml.Guideline.ValidationType;
import org.sonar.plugins.webscanner.toetstool.xml.ToetstoolReport;

/**
 * @author Matthijs Galesloot
 * @since 0.1
 */
public final class ToetstoolSensor implements Sensor {

  public static final String SONAR_TOETSTOOL_URL = "sonar.toetstool.url";

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

    HtmlProjectFileSystem fileSystem = new HtmlProjectFileSystem(project);
    prepareScanning(fileSystem.getSourceDirs());

    // create validator
    ToetsToolValidator validator = new ToetsToolValidator((String) project.getProperty(SONAR_TOETSTOOL_URL), 
        fileSystem.getSourceDirs().get(0), new File(project.getFileSystem().getBuildDir() + "/html"));

    // configure proxy
    if (session.getSettings().getActiveProxy() != null) {
      validator.setProxyHost(session.getSettings().getActiveProxy().getHost());
      validator.setProxyPort(session.getSettings().getActiveProxy().getPort());
    }

    // start the html scanner
    HtmlFileScanner htmlFileScanner = new HtmlFileScanner(validator);
    htmlFileScanner.validateFiles(fileSystem.getFiles(), WebScannerPlugin.getNrOfSamples(project));

    // save analysis to sonar
    saveResults(project, sensorContext, validator, fileSystem.getFiles());
  }

  private boolean hasToetstoolRules() {
    for (ActiveRule activeRule : profile.getActiveRules()) {
      if (ToetstoolRuleRepository.REPOSITORY_KEY.equals(activeRule.getRepositoryKey())) {
        return true;
      }
    }
    return false;
  }

  private void prepareScanning(List<File> sourceDirs) {

    for (File sourceDir : sourceDirs) {
      if ( !sourceDir.exists()) {
        LOG.error("Missing HTML directory: " + sourceDir.getPath());
        continue;
      }

      LOG.info("HTML Dir:" + sourceDir);
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
  private void saveResults(Project project, SensorContext sensorContext, ToetsToolValidator validator, List<InputFile> inputfiles) {

    int numValid = 0;
    int numFiles = 0;

    List<File> reportFiles = new ArrayList<File>();

    for (InputFile inputfile : inputfiles) {
      org.sonar.api.resources.File htmlFile = HtmlProjectFileSystem.fromIOFile(inputfile, project);
      File reportFile = validator.reportFile(inputfile.getFile());

      if (reportFile.exists()) {
        reportFiles.add(reportFile);
        boolean isValid = readValidationReport(sensorContext, reportFile, htmlFile);
        if (isValid) {
          numValid++;
        }
      } else {
        LOG.warn("Missing reportfile for file " + inputfile.getRelativePath());
      }
      numFiles++;
    }

    new ToetsToolReportBuilder(validator).buildReports(reportFiles);

    double percentageValid = numFiles > 0 ? (double) numValid / numFiles : 1;
    sensorContext.saveMeasure(HtmlMetrics.TOETSTOOL_VALIDITY, percentageValid * 100);
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
