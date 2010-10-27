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

package org.sonar.plugins.web.toetstool;

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
import org.sonar.plugins.web.ProjectConfiguration;
import org.sonar.plugins.web.html.FileSet;
import org.sonar.plugins.web.html.HtmlMetrics;
import org.sonar.plugins.web.language.Web;
import org.sonar.plugins.web.language.WebFile;
import org.sonar.plugins.web.rules.toetstool.ToetstoolRuleRepository;
import org.sonar.plugins.web.toetstool.xml.Guideline;
import org.sonar.plugins.web.toetstool.xml.Guideline.ValidationType;
import org.sonar.plugins.web.toetstool.xml.ToetstoolReport;

/**
 * @author Matthijs Galesloot
 * @since 0.2
 */
public final class ToetstoolSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(ToetstoolSensor.class);

  private final RulesProfile profile;
  private final RuleFinder ruleFinder;

  public ToetstoolSensor(RulesProfile profile, RuleFinder ruleFinder) {
    LOG.info("Profile: " + profile.getName());
    this.profile = profile;
    this.ruleFinder = ruleFinder;
  }

  /**
   * Find Toetstool Validation reports in the source tree and save violations to Sonar.
   * The Toetstool reports have file extension .ttr.
   */
  public void analyse(Project project, SensorContext sensorContext) {

    ProjectConfiguration projectConfiguration = new ProjectConfiguration(project);
    projectConfiguration.addSourceDir();
    File htmlDir = new File(project.getFileSystem().getBasedir() + "/" + projectConfiguration.getSourceDir());
    if (!htmlDir.exists()) {
      return;
      // throw new SonarException("Missing HTML directory: " + htmlDir.getPath());
    }

    LOG.info("HTML Dir:" + htmlDir);

    int numValid = 0;

    Collection<File> files = FileSet.getReportFiles(htmlDir, ToetstoolReport.REPORT_SUFFIX);

    for (File reportFile : files) {
      ToetstoolReport report = ToetstoolReport.fromXml(reportFile);

      if (report.getReport().getCounters().getError() == 0) {
        numValid++;
      }

      // derive name of resource from name of report
      File file = new File(StringUtils.substringBefore(report.getReportFile().getPath(), ToetstoolReport.REPORT_SUFFIX));
      WebFile resource = WebFile.fromIOFile(file, project.getFileSystem().getSourceDirs());

      // save errors
      for (Guideline guideline : report.getReport().getGuidelines()) {
        if (guideline.getType() == ValidationType.error ) {
          addViolation(sensorContext, resource, guideline, true);
        }
      }
      // save warnings
      for (Guideline guideline : report.getReport().getGuidelines()) {
        if (guideline.getType() == ValidationType.warning) {
          addViolation(sensorContext, resource, guideline, false);
        }
      }
    }

    double percentageValid = files.size() > 0 ? (double) (files.size() - numValid) / files.size() : 100;
    sensorContext.saveMeasure(HtmlMetrics.W3C_MARKUP_VALIDITY, percentageValid);
  }

  private void addViolation(SensorContext sensorContext, WebFile resource, Guideline guideline, boolean error) {
    String ruleKey = guideline.getRef();
    Rule rule = ruleFinder.findByKey(ToetstoolRuleRepository.REPOSITORY_KEY, ruleKey);
    if (rule != null) {
      Violation violation = Violation.create(rule, resource);
      violation.setMessage((error ? "" : "Warning: ") + guideline.getRemark());
      sensorContext.saveViolation(violation);
      LOG.debug(resource.getName() + ": " + guideline.getRef() + ":" + guideline.getRemark());
    } else {
      LOG.warn("Could not find Toetstool Rule " + guideline.getRef() + ", Message = " + guideline.getRemark());
    }
  }

  /**
   * This sensor only executes on Web projects with W3C Markup rules.
   */
  public boolean shouldExecuteOnProject(Project project) {
    return isEnabled(project) && Web.INSTANCE.equals(project.getLanguage()) && hasToetstoolRules();
  }

  private boolean hasToetstoolRules() {
    for (ActiveRule activeRule : profile.getActiveRules()) {
      if (ToetstoolRuleRepository.REPOSITORY_KEY.equals(activeRule.getRepositoryKey())) {
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
