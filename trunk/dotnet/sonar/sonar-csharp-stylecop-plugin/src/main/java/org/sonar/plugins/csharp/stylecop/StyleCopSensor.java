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
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.utils.SonarException;
import org.sonar.dotnet.tools.commons.utils.FileFinder;
import org.sonar.dotnet.tools.stylecop.StyleCopCommandBuilder;
import org.sonar.dotnet.tools.stylecop.StyleCopException;
import org.sonar.dotnet.tools.stylecop.StyleCopRunner;
import org.sonar.plugins.csharp.api.CSharpConfiguration;
import org.sonar.plugins.csharp.api.CSharpConstants;
import org.sonar.plugins.csharp.api.MicrosoftWindowsEnvironment;
import org.sonar.plugins.csharp.api.sensor.AbstractRegularCSharpSensor;
import org.sonar.plugins.csharp.stylecop.profiles.StyleCopProfileExporter;

/**
 * Collects the StyleCop reporting into sonar.
 */
@DependsUpon(CSharpConstants.CSHARP_CORE_EXECUTED)
public class StyleCopSensor extends AbstractRegularCSharpSensor {

  private static final Logger LOG = LoggerFactory.getLogger(StyleCopSensor.class);

  private ProjectFileSystem fileSystem;
  private RulesProfile rulesProfile;
  private StyleCopProfileExporter profileExporter;
  private StyleCopResultParser styleCopResultParser;
  private CSharpConfiguration configuration;

  /**
   * Constructs a {@link StyleCopSensor}.
   * 
   * @param fileSystem
   * @param ruleFinder
   * @param styleCopRunner
   * @param profileExporter
   * @param rulesProfile
   */
  public StyleCopSensor(ProjectFileSystem fileSystem, RulesProfile rulesProfile, StyleCopProfileExporter profileExporter,
      StyleCopResultParser styleCopResultParser, CSharpConfiguration configuration, MicrosoftWindowsEnvironment microsoftWindowsEnvironment) {
    super(microsoftWindowsEnvironment, "StyleCop", configuration.getString(StyleCopConstants.MODE, ""));
    this.fileSystem = fileSystem;
    this.rulesProfile = rulesProfile;
    this.profileExporter = profileExporter;
    this.styleCopResultParser = styleCopResultParser;
    this.configuration = configuration;
  }

  /**
   * {@inheritDoc}
   */
  public void analyse(Project project, SensorContext context) {
    if (rulesProfile.getActiveRulesByRepository(StyleCopConstants.REPOSITORY_KEY).isEmpty()) {
      LOG.warn("/!\\ SKIP StyleCop analysis: no rule defined for StyleCop in the \"{}\" profil.", rulesProfile.getName());
      return;
    }

    styleCopResultParser.setEncoding(fileSystem.getSourceCharset());
    final File reportFile;
    File projectDir = project.getFileSystem().getBasedir();
    String reportDefaultPath = getMicrosoftWindowsEnvironment().getWorkingDirectory() + "/" + StyleCopConstants.STYLECOP_REPORT_XML;
    if (MODE_REUSE_REPORT.equalsIgnoreCase(executionMode)) {
      String reportPath = configuration.getString(StyleCopConstants.REPORTS_PATH_KEY, reportDefaultPath);
      reportFile = FileFinder.browse(projectDir, reportPath);
      LOG.info("Reusing StyleCop report: " + reportFile);
    } else {
      // prepare config file for StyleCop
      File styleCopConfigFile = generateConfigurationFile();
      // run StyleCop
      try {
        File tempDir = new File(getMicrosoftWindowsEnvironment().getCurrentSolution().getSolutionDir(), getMicrosoftWindowsEnvironment()
            .getWorkingDirectory());
        StyleCopRunner runner = StyleCopRunner.create(
            configuration.getString(StyleCopConstants.INSTALL_DIR_KEY, StyleCopConstants.INSTALL_DIR_DEFVALUE),
            getMicrosoftWindowsEnvironment().getDotnetSdkDirectory().getAbsolutePath(), tempDir.getAbsolutePath());
        launchStyleCop(project, runner, styleCopConfigFile);
      } catch (StyleCopException e) {
        throw new SonarException("StyleCop execution failed.", e);
      }
      reportFile = new File(projectDir, reportDefaultPath);
    }

    // and analyse results
    analyseResults(reportFile);
  }

  protected void launchStyleCop(Project project, StyleCopRunner runner, File styleCopConfigFile) throws StyleCopException {
    StyleCopCommandBuilder builder = runner.createCommandBuilder(getMicrosoftWindowsEnvironment().getCurrentSolution(),
        getVSProject(project));
    builder.setConfigFile(styleCopConfigFile);
    builder.setReportFile(new File(fileSystem.getSonarWorkingDirectory(), StyleCopConstants.STYLECOP_REPORT_XML));
    runner.execute(builder, configuration.getInt(StyleCopConstants.TIMEOUT_MINUTES_KEY, StyleCopConstants.TIMEOUT_MINUTES_DEFVALUE));
  }

  protected File generateConfigurationFile() {
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

  private void analyseResults(File reportFile) {
    if (reportFile.exists()) {
      LOG.debug("StyleCop report found at location {}", reportFile);
      styleCopResultParser.parse(reportFile);
    } else {
      LOG.warn("No StyleCop report found for path {}", reportFile);
    }
  }
}
