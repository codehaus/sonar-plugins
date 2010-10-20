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

package org.sonar.plugins.web.html;

import java.io.File;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.web.ProjectConfiguration;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.language.WebProperties;
import org.sonar.plugins.web.markupvalidation.MarkupMessage;
import org.sonar.plugins.web.markupvalidation.MarkupReport;
import org.sonar.plugins.web.rules.markup.MarkupRuleRepository;

/**
 * @author Matthijs Galesloot
 * @since 0.2
 */
public final class HtmlSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(HtmlSensor.class);

  private final RulesProfile profile;
  private final RuleFinder ruleFinder;

  public HtmlSensor(RulesProfile profile, RuleFinder ruleFinder) {
    LOG.info("Profile: " + profile.getName());
    this.profile = profile;
    this.ruleFinder = ruleFinder;
  }

  /**
   * Find W3C Validation Markup reports in the source tree and save violations to Sonar.
   * The Markup reports have file extension .mur.
   */
  public void analyse(Project project, SensorContext sensorContext) {

    ProjectConfiguration projectConfiguration = new ProjectConfiguration(project);
    projectConfiguration.addSourceDir();
    File htmlDir = new File(project.getFileSystem().getBasedir() + "/" + projectConfiguration.getSourceDir());
    if (!htmlDir.exists()) {
      throw new SonarException("Missing HTML directory: " + htmlDir.getPath());
    }

    LOG.info("HTML Dir:" + htmlDir);
    MessageFilter messageFilter = new MessageFilter(project.getProperty(WebProperties.EXCLUDE_VIOLATIONS));

    int numValid = 0;

    Collection<File> files = FileSet.getReportFiles(htmlDir, MarkupReport.REPORT_SUFFIX);

    for (File reportFile : files) {
      MarkupReport report = MarkupReport.fromXml(reportFile);

      if (report.isValid()) {
        numValid++;
      }

      // derive name of resource from name of report
      File file = new File(StringUtils.substringBefore(report.getReportFile().getPath(), MarkupReport.REPORT_SUFFIX));
      WebFile resource = WebFile.fromIOFile(file, project.getFileSystem().getSourceDirs());

      // save errors
      for (MarkupMessage error : report.getErrors()) {
        if (messageFilter.accept(error)) {
          addViolation(sensorContext, resource, error, true);
        }
      }
      // save warnings
      for (MarkupMessage warning : report.getWarnings()) {
        if (messageFilter.accept(warning)) {
          addViolation(sensorContext, resource, warning, false);
        }
      }
    }

    double percentageValid = files.size() > 0 ? (double) (files.size() - numValid) / files.size() : 100;
    sensorContext.saveMeasure(HtmlMetrics.W3C_MARKUP_VALIDITY, percentageValid);
  }

  private void addViolation(SensorContext sensorContext, WebFile resource, MarkupMessage message, boolean error) {
    String ruleKey = message.getMessageId();
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
   * This sensor only executes on Web projects with W3C Markup rules.
   */
  public boolean shouldExecuteOnProject(Project project) {
    return isEnabled(project) && Web.INSTANCE.equals(project.getLanguage()) && hasMarkupRules();
  }

  private boolean hasMarkupRules() {
    for (ActiveRule activeRule : profile.getActiveRules()) {
      if (MarkupRuleRepository.REPOSITORY_KEY.equals(activeRule.getRepositoryKey())) {
        return true;
      }
    }
    return false;
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
