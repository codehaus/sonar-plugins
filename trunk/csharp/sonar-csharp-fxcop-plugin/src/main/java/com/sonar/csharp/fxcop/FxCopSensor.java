/*
 * Sonar C# Plugin :: FxCop
 * Copyright (C) 2010 SonarSource
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

package com.sonar.csharp.fxcop;

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
import org.sonar.plugins.csharp.api.CSharpResourcesBridge;

import com.sonar.csharp.fxcop.profiles.FxCopProfileExporter;
import com.sonar.csharp.fxcop.results.FxCopResultParser;
import com.sonar.csharp.fxcop.runner.FxCopRunner;

/**
 * Collects the FXCop reporting into sonar.
 */
public class FxCopSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(FxCopSensor.class);
  private static final String FXCOP_RULES_FILE = "sonar.FxCop";

  private ProjectFileSystem fileSystem;
  private RuleFinder ruleFinder;
  private FxCopRunner fxCopRunner;
  private FxCopProfileExporter profileExporter;
  private RulesProfile rulesProfile;

  /**
   * Constructs a {@link FxCopSensor}.
   * 
   * @param fileSystem
   * @param ruleFinder
   * @param fxCopRunner
   * @param profileExporter
   * @param rulesProfile
   */
  public FxCopSensor(ProjectFileSystem fileSystem, RuleFinder ruleFinder, FxCopRunner fxCopRunner, FxCopProfileExporter profileExporter,
      RulesProfile rulesProfile) {
    this.fileSystem = fileSystem;
    this.ruleFinder = ruleFinder;
    this.fxCopRunner = fxCopRunner;
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
    // prepare config file for FxCop
    File fxCopConfigFile = generateConfigurationFile();

    // run FxCop
    fxCopRunner.execute(fxCopConfigFile);

    // and analyse results
    analyseResults(project, context);
  }

  private File generateConfigurationFile() {
    File configFile = new File(fileSystem.getSonarWorkingDirectory(), FXCOP_RULES_FILE);
    FileWriter writer = null;
    try {
      writer = new FileWriter(configFile);
      profileExporter.exportProfile(rulesProfile, writer);
      writer.flush();
    } catch (IOException e) {
      throw new SonarException("Error while generating the FxCop configuration file by exporting the Sonar rules.", e);
    } finally {
      IOUtils.closeQuietly(writer);
    }
    return configFile;
  }

  private void analyseResults(Project project, SensorContext context) {
    final String[] reportFileNames = new String[] { FxCopConstants.FXCOP_REPORT_XML /* , FxCopConstants.SL_FXCOP_REPORT_XML */};
    File dir = project.getFileSystem().getBuildDir();

    for (String reportFileName : reportFileNames) {
      File report = new File(dir, reportFileName);
      if (report.exists()) {
        LOG.info("FxCop report found at location {}", report);
        FxCopResultParser parser = new FxCopResultParser(project, context, ruleFinder, CSharpResourcesBridge.getInstance());
        parser.setEncoding(fileSystem.getSourceCharset());
        parser.parse(report);
      } else {
        LOG.info("No FxCop report found for path {}", report);
      }
    }
  }

}