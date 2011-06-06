/*
 * Sonar C# Plugin :: FxCop
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

package org.sonar.plugins.csharp.fxcop;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.csharp.api.CSharpConfiguration;
import org.sonar.plugins.csharp.api.CSharpConstants;
import org.sonar.plugins.csharp.api.MicrosoftWindowsEnvironment;
import org.sonar.plugins.csharp.api.sensor.AbstractCSharpSensor;
import org.sonar.plugins.csharp.fxcop.profiles.FxCopProfileExporter;
import org.sonar.plugins.csharp.fxcop.runner.FxCopRunner;

import com.google.common.collect.Lists;

/**
 * Collects the FXCop reporting into sonar.
 */
@DependsUpon(CSharpConstants.CSHARP_CORE_EXECUTED)
public class FxCopSensor extends AbstractCSharpSensor {

  private static final Logger LOG = LoggerFactory.getLogger(FxCopSensor.class);

  private ProjectFileSystem fileSystem;
  private RulesProfile rulesProfile;
  private FxCopRunner fxCopRunner;
  private FxCopProfileExporter profileExporter;
  private FxCopResultParser fxCopResultParser;
  private CSharpConfiguration configuration;
  private String executionMode;

  /**
   * Constructs a {@link FxCopSensor}.
   * 
   * @param fileSystem
   * @param ruleFinder
   * @param fxCopRunner
   * @param profileExporter
   * @param rulesProfile
   */
  public FxCopSensor(ProjectFileSystem fileSystem, RulesProfile rulesProfile, FxCopRunner fxCopRunner,
      FxCopProfileExporter profileExporter, FxCopResultParser fxCopResultParser, CSharpConfiguration configuration,
      MicrosoftWindowsEnvironment microsoftWindowsEnvironment) {
    super(microsoftWindowsEnvironment);
    this.fileSystem = fileSystem;
    this.rulesProfile = rulesProfile;
    this.fxCopRunner = fxCopRunner;
    this.profileExporter = profileExporter;
    this.fxCopResultParser = fxCopResultParser;
    this.configuration = configuration;
    this.executionMode = configuration.getString(FxCopConstants.MODE, "");
  }

  /**
   * {@inheritDoc}
   */
  public boolean shouldExecuteOnProject(Project project) {
    boolean skipMode = FxCopConstants.MODE_SKIP.equalsIgnoreCase(executionMode);
    if (skipMode) {
      LOG.info("FxCop plugin won't execute as it is set to 'skip' mode.");
    }
    return super.shouldExecuteOnProject(project) && !skipMode;
  }

  /**
   * {@inheritDoc}
   */
  public void analyse(Project project, SensorContext context) {
    if (rulesProfile.getActiveRulesByRepository(FxCopConstants.REPOSITORY_KEY).isEmpty()) {
      LOG.warn("/!\\ SKIP FxCop analysis: no rule defined for FxCop in the \"{}\" profil.", rulesProfile.getName());
      return;
    }

    fxCopResultParser.setEncoding(fileSystem.getSourceCharset());

    if ( !FxCopConstants.MODE_REUSE_REPORT.equalsIgnoreCase(executionMode)) {
      // prepare config file for FxCop
      File fxCopConfigFile = generateConfigurationFile();
      // and run FxCop
      fxCopRunner.execute(fxCopConfigFile);
    }

    // and analyse results
    Collection<File> reportFiles = getReportFilesList();
    analyseResults(reportFiles);
  }

  protected File generateConfigurationFile() {
    File configFile = new File(fileSystem.getSonarWorkingDirectory(), FxCopConstants.FXCOP_RULES_FILE);
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

  protected Collection<File> getReportFilesList() {
    Collection<File> reportFiles = Lists.newArrayList();
    if (FxCopConstants.MODE_REUSE_REPORT.equalsIgnoreCase(executionMode)) {
      File targetDir = fileSystem.getBuildDir();
      String[] reportsPath = configuration.getStringArray(FxCopConstants.REPORTS_PATH_KEY);
      for (int i = 0; i < reportsPath.length; i++) {
        reportFiles.add(new File(targetDir, reportsPath[i]));
      }
      if (reportFiles.isEmpty()) {
        LOG.warn("No report to analyse whereas FxCop runs in 'reuseReport' mode. Please specify at least on report to analyse.");
      }
    } else {
      File sonarDir = fileSystem.getSonarWorkingDirectory();
      reportFiles.add(new File(sonarDir, FxCopConstants.FXCOP_REPORT_XML));
    }
    return reportFiles;
  }

  protected void analyseResults(Collection<File> reportFiles) {
    for (File reportFile : reportFiles) {
      if (reportFile.exists()) {
        LOG.debug("FxCop report found at location {}", reportFile);
        fxCopResultParser.parse(reportFile);
      } else {
        LOG.warn("No FxCop report found for path {}", reportFile);
      }
    }
  }

}