/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SQLi
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.php.codesniffer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;
import org.sonar.plugins.php.core.Php;
import org.sonar.plugins.php.core.PhpFile;

/**
 * The Class PhpCodesnifferPluginSensor.
 */
public class PhpCodesnifferSensor implements Sensor {

  /** The logger. */
  private static final Logger LOG = LoggerFactory.getLogger(PhpCodesnifferSensor.class);

  /** The rules profile. */
  private RulesProfile profile;

  /** The rules finder. */
  private RuleFinder ruleFinder;

  /** The plugin configuration. */
  private PhpCodesnifferConfiguration config;

  /**
   * The associated language.
   */
  private Php php;

  /**
   * Default constructor used for tests only.
   */
  PhpCodesnifferSensor() {
    super();
  }

  /**
   * Instantiates a new php codesniffer sensor.
   * 
   * @param rulesManager
   *          the rules manager
   */
  public PhpCodesnifferSensor(RulesProfile profile, RuleFinder ruleFinder, Php php) {
    super();
    this.ruleFinder = ruleFinder;
    this.php = php;
    this.profile = profile;
  }

  /**
   * Launches the external tool (if configured so) and analyze result file.
   * 
   * @param project
   *          the project
   * @param context
   *          the context
   * 
   * @see org.sonar.api.batch.Sensor#analyse(org.sonar.api.resources.Project, org.sonar.api.batch.SensorContext)
   */
  public void analyse(Project project, SensorContext context) {
    File report = config.getReportFile();
    LOG.info("Findbugs output report: " + report.getAbsolutePath());
    PhpCodesnifferViolationsXmlParser reportParser = new PhpCodesnifferViolationsXmlParser(report);
    List<PhpCodeSnifferViolation> violations = reportParser.getViolations();
    List<Violation> contextViolations = new ArrayList<Violation>();
    for (PhpCodeSnifferViolation violation : violations) {
      Rule rule = ruleFinder.findByKey(PhpCodeSnifferRuleRepository.REPOSITORY_KEY, violation.getRuleKey());
      PhpFile resource = (PhpFile) context.getResource(PhpFile.fromAbsolutePath(violation.getFileName(), project));
      if (context.getResource(resource) != null) {
        Violation v = Violation.create(rule, resource).setLineId(violation.getLine()).setMessage(violation.getLongMessage());
        contextViolations.add(v);
      }
      context.saveViolations(contextViolations);
    }

  }

  /**
   * Gets the configuration.
   * 
   * @param project
   *          the project
   * 
   * @return the configuration
   */
  private PhpCodesnifferConfiguration getConfiguration(Project project) {
    if (config == null) {
      config = new PhpCodesnifferConfiguration(project);
    }
    return config;
  }

  /**
   * Returns <code>true</code> if the given project language is PHP and the project configuration is set to allow plugin to run.
   * 
   * @param project
   *          the project
   * 
   * @return true, if should execute on project
   * 
   * @see org.sonar.api.batch.CheckProject#shouldExecuteOnProject(org.sonar.api .resources.Project)
   */
  public boolean shouldExecuteOnProject(Project project) {
    return getConfiguration(project).isShouldRun() && project.getLanguage().equals(php);
    // && ( !profile.getActiveRulesByRepository(PhpCodeSnifferRuleRepository.REPOSITORY_KEY).isEmpty() || project
    // .getReuseExistingRulesConfig()) && project.getPom() != null;
  }

  /**
   * To string.
   * 
   * @return the string
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}