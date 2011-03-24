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

package org.sonar.plugins.csharp.stylecop.runner;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.csharp.api.MicrosoftWindowsEnvironment;
import org.sonar.plugins.csharp.stylecop.StyleCopConstants;

/**
 * Class that runs the StyleCop program.
 */
public class StyleCopRunner implements BatchExtension {

  private static final Logger LOG = LoggerFactory.getLogger(StyleCopRunner.class);

  private ProjectFileSystem projectFileSystem;
  private StyleCopCommand command;
  private MicrosoftWindowsEnvironment microsoftWindowsEnvironment;
  private MsBuildFileGenerator msBuildFileGenerator;
  private int timeoutMinutes;

  /**
   * Constructs a {@link StyleCopRunner}.
   * 
   * @param configuration
   *          StyleCop configuration elements
   * @param fileSystem
   *          the file system of the project
   */
  public StyleCopRunner(Configuration configuration, ProjectFileSystem projectFileSystem,
      MicrosoftWindowsEnvironment microsoftWindowsEnvironment) {
    this.projectFileSystem = projectFileSystem;
    this.microsoftWindowsEnvironment = microsoftWindowsEnvironment;
    this.command = new StyleCopCommand(projectFileSystem);
    this.msBuildFileGenerator = new MsBuildFileGenerator(configuration);
    timeoutMinutes = configuration.getInt(StyleCopConstants.TIMEOUT_MINUTES_KEY, StyleCopConstants.TIMEOUT_MINUTES_DEFVALUE);
  }

  /**
   * Launches the StyleCop program.
   * 
   * @param styleCopConfigFile
   *          the StyleCop config file to use
   */
  public void execute(File styleCopConfigFile) {
    LOG.debug("Generating MSBuild file...");
    msBuildFileGenerator.setVisualStudioSolution(microsoftWindowsEnvironment.getCurrentSolution());
    File msBuildFile = msBuildFileGenerator.generateFile(projectFileSystem.getSonarWorkingDirectory());
    LOG.debug("  -> Success: {}", msBuildFile.getAbsolutePath());

    LOG.debug("Executing StyleCop program...");
    command.setStyleCopConfigFile(styleCopConfigFile);
    command.setMsBuildFile(msBuildFile);
    command.setDotnetSdkDirectory(microsoftWindowsEnvironment.getDotnetSdkDirectory());
    new CommandExecutor().execute(command.toArray(), TimeUnit.MINUTES.toSeconds(timeoutMinutes));
  }

}
