/*
 * Sonar C# Plugin :: StyleCop
 * Copyright (C) 2010 Jose Chillan, Alexandre Victoor and SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.csharp.stylecop;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.csharp.stylecop.profiles.StyleCopProfileExporter;
import org.sonar.plugins.csharp.stylecop.runner.StyleCopRunner;

/**
 * Collects the FXCop reporting into sonar.
 */
public class StyleCopSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(StyleCopSensor.class);

  private ProjectFileSystem fileSystem;
  private RuleFinder ruleFinder;
  private StyleCopRunner styleCopRunner;
  private StyleCopProfileExporter profileExporter;
  private RulesProfile rulesProfile;

  /**
   * Constructs a {@link StyleCopSensor}.
   * 
   * @param fileSystem
   * @param ruleFinder
   * @param styleCopRunner
   * @param profileExporter
   * @param rulesProfile
   */
  public StyleCopSensor(ProjectFileSystem fileSystem, RuleFinder ruleFinder, StyleCopRunner styleCopRunner,
      StyleCopProfileExporter profileExporter, RulesProfile rulesProfile) {
    this.fileSystem = fileSystem;
    this.ruleFinder = ruleFinder;
    this.styleCopRunner = styleCopRunner;
    this.profileExporter = profileExporter;
    this.rulesProfile = rulesProfile;
  }

  /**
   * {@inheritDoc}
   */
  public boolean shouldExecuteOnProject(Project project) {
    return project.getLanguageKey().equals("cs");
  }

  /**
   * {@inheritDoc}
   */
  public void analyse(Project project, SensorContext context) {
    // prepare config file for StyleCop
    File styleCopConfigFile = generateConfigurationFile();

    // run StyleCop
    styleCopRunner.execute(styleCopConfigFile);

    // and analyse results
    analyseResults(project, context);
  }

  private File generateConfigurationFile() {
    File configFile = new File(fileSystem.getSonarWorkingDirectory(), StyleCopConstants.STYLECOP_RULES_FILE);
    FileWriter writer = null;
    try {
      writer = new FileWriter(configFile);
      profileExporter.exportProfile(rulesProfile, writer);
      writer.flush();
    } catch (IOException e) {
      throw new SonarException("Error while generating the StyleCop configuration file by exporting the Sonar rules.", e);
    } finally {
      IOUtils.closeQuietly(writer);
    }
    return configFile;
  }

  private void analyseResults(Project project, SensorContext context) {
    File report = new File(fileSystem.getSonarWorkingDirectory(), StyleCopConstants.STYLECOP_REPORT_XML);
    if (report.exists()) {
      LOG.info("StyleCop report found at location {}", report.getAbsolutePath());
      StyleCopResultParser parser = new StyleCopResultParser(project, context, ruleFinder);
      parser.setEncoding(fileSystem.getSourceCharset());
      parser.parse(report);
    } else {
      LOG.error("StyleCop report cound not be found: {}", report.getAbsolutePath());
    }
  }

}