/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 Akram Ben Aissi
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

package org.sonar.plugins.php.pmd;

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
import org.sonar.plugins.php.codesniffer.PhpCodeSnifferRuleRepository;
import org.sonar.plugins.php.core.Php;
import org.sonar.plugins.php.core.PhpFile;

/**
 * The plugin entry point.
 */
public class PhpmdSensor implements Sensor {

  /** The logger. */
  private static final Logger LOG = LoggerFactory.getLogger(PhpmdSensor.class);

  /** The rules profile. */
  private RulesProfile profile;

  /** The rules finder. */
  private RuleFinder ruleFinder;

  /** The plugin configuration. */
  private PhpmdConfiguration config;
  /**
   * The associated language.
   */
  private Php php;

  /**
   * Instantiates a new php pmd sensor.
   * 
   * @param rulesManager
   *          the rules manager
   */
  public PhpmdSensor(RulesProfile profile, RuleFinder ruleFinder, Php php) {
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
    // If configured so, execute the tool
    if ( !getConfiguration(project).isAnalyseOnly()) {
      PhpmdExecutor executor = new PhpmdExecutor(config);
      executor.execute();
    }
    File report = config.getReportFile();
    LOG.info("Phpmd  report file: " + report.getAbsolutePath());
    PhpmdViolationsXmlParser reportParser = new PhpmdViolationsXmlParser(report);
    List<PhpmdViolation> violations = reportParser.getViolations();
    List<Violation> contextViolations = new ArrayList<Violation>();
    for (PhpmdViolation violation : violations) {
      Rule rule = ruleFinder.findByKey(PhpmdRuleRepository.PHPMD_REPOSITORY_KEY, violation.getRuleKey());
      if (rule != null) {
        PhpFile resource = (PhpFile) context.getResource(PhpFile.fromAbsolutePath(violation.getFileName(), project));
        if (context.getResource(resource) != null) {
          Violation v = Violation.create(rule, resource).setLineId(violation.getBeginLine()).setMessage(violation.getLongMessage());
          contextViolations.add(v);
          LOG.debug("Violation found: " + v);
        }
      }
    }
    context.saveViolations(contextViolations);
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
    boolean sensorShouldRun = getConfiguration(project).isShouldRun();
    boolean projectIsPhp = project.getLanguage().equals(php);
    boolean profileContainsActiveRules = !profile.getActiveRulesByRepository(PhpCodeSnifferRuleRepository.PHPCS_REPOSITORY_KEY).isEmpty();
    boolean reuseRules = project.getReuseExistingRulesConfig();
    boolean pomNotNull = project.getPom() != null;
    return sensorShouldRun && projectIsPhp && (profileContainsActiveRules || reuseRules) && pomNotNull;
  }

  /***
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  /**
   * Gets the configuration. If config field is null initialize it, other way only returns it.
   * 
   * @param project
   *          the project
   * @return the configuration
   */
  private PhpmdConfiguration getConfiguration(Project project) {
    if (config == null) {
      config = new PhpmdConfiguration(project);
    }
    return config;
  }
}