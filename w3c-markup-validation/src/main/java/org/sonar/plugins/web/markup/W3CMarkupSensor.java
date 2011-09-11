/*
 * Sonar W3C Markup Validation Plugin
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
package org.sonar.plugins.web.markup;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.InputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.web.api.ProjectFileManager;
import org.sonar.plugins.web.api.WebConstants;
import org.sonar.plugins.web.markup.rules.MarkupRuleRepository;
import org.sonar.plugins.web.markup.validation.MarkupMessage;
import org.sonar.plugins.web.markup.validation.MarkupReport;
import org.sonar.plugins.web.markup.validation.MarkupReportBuilder;
import org.sonar.plugins.web.markup.validation.MarkupValidator;

/**
 * Sensor using the W3C Validator.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
public final class W3CMarkupSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(W3CMarkupSensor.class);

  private final RulesProfile profile;
  private final RuleFinder ruleFinder;

  public W3CMarkupSensor(Project project, RulesProfile profile, RuleFinder ruleFinder) {
    this.profile = profile;
    this.ruleFinder = ruleFinder;
  }

  private void addViolation(SensorContext sensorContext, org.sonar.api.resources.File resource, MarkupMessage message, boolean error) {
    String ruleKey = makeIdentifier(message.getMessageId());
    Rule rule = ruleFinder.findByKey(MarkupRuleRepository.REPOSITORY_KEY, ruleKey);
    if (rule != null) {
      Integer lineId = message.getLine();
      if (lineId != null && lineId == 0) {
        lineId = null;
      }
      Violation violation = Violation.create(rule, resource).setLineId(lineId);
      violation.setMessage((error ? "" : "Warning: ") + message.getMessage());
      sensorContext.saveViolation(violation);
      LOG.debug(resource.getName() + ": " + message.getMessageId() + ":" + message.getMessage());
    } else {
      LOG.warn("Could not find Markup Rule " + message.getMessageId() + ", Message = " + message.getMessage());
    }
  }

  /**
   * Validate HTML files with W3C Markup Validator and save violations to Sonar.
   */
  public void analyse(Project project, SensorContext sensorContext) {

    LOG.info("Profile: " + profile.getName());

    ProjectFileManager fileManager = new ProjectFileManager(project);

    // create validator
    MarkupValidator validator = new MarkupValidator(project.getConfiguration(), new File(project.getFileSystem().getBuildDir() + "/"
        + project.getArtifactId()));

    // start the validation
    validator.validateFiles(fileManager.getFiles());

    // save analysis to sonar
    saveResults(project, sensorContext, fileManager, validator, fileManager.getFiles());
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

  private void saveResults(Project project, SensorContext sensorContext, ProjectFileManager fileManager, MarkupValidator validator,
      List<InputFile> inputfiles) {
    List<File> reportFiles = new ArrayList<File>();

    for (InputFile inputfile : inputfiles) {
      org.sonar.api.resources.File htmlFile = fileManager.fromIOFile(inputfile);
      File reportFile = validator.reportFile(inputfile);

      if (reportFile.exists()) {
        reportFiles.add(reportFile);

        readValidationReport(sensorContext, reportFile, htmlFile);
      } else {
        LOG.warn("Missing reportfile for file " + inputfile.getRelativePath());
      }

      File propsFile = new File(inputfile.getFile().getPath() + ".txt");
      if (propsFile.exists()) {
        LOG.info("Save properties");
        Properties properties = new Properties();
        try {
          properties.load(new FileInputStream(propsFile));
          String measure = format(properties);
          LOG.info("Save measure:" + measure);
          sensorContext.saveMeasure(htmlFile, new Measure(PageMetrics.PAGE, measure));
        } catch (Exception e) {
          LOG.error("Could not save measure", e);
        }
      }
    }

    new MarkupReportBuilder().buildReports(project.getArtifactId(), reportFiles);
  }

  private String format(Properties properties) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;

    List<String> keys = new ArrayList(properties.keySet());
    Collections.sort(keys);
    for (String key : keys) {
      if ( !first) {
        sb.append('\n');
      }
      sb.append(key);
      sb.append('=');
      sb.append(properties.get(key));
      first = false;
    }
    return sb.toString();
  }

  /**
   * This sensor only executes on Web projects with W3C Markup rules.
   */
  public boolean shouldExecuteOnProject(Project project) {
    return WebConstants.LANGUAGE_KEY.equals(project.getLanguageKey()) && hasMarkupRules();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
