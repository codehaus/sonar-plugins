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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.utils.command.Command;

/**
 * Class used to build the command line to run StyleCop.
 */
public class StyleCopCommandBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(StyleCopCommandBuilder.class);

  private ProjectFileSystem fileSystem;
  private File styleCopConfigFile;
  private File msBuildFile;
  private File dotnetSdkDirectory;

  /**
   * Constructs a {@link StyleCopCommandBuilder} object.
   * 
   * @param fileSystem
   *          the file system of the project
   */
  public StyleCopCommandBuilder(ProjectFileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  /**
   * Sets StyleCop configuration file that must be used to perform the analysis. It is mandatory.
   * 
   * @param styleCopConfigFile
   *          the file
   */
  public void setStyleCopConfigFile(File styleCopConfigFile) {
    this.styleCopConfigFile = styleCopConfigFile;
  }

  /**
   * Sets the MSBuild file that will be used to launch StyleCop. It is mandatory.
   * 
   * @param msBuildFile
   *          the file
   */
  public void setMsBuildFile(File msBuildFile) {
    this.msBuildFile = msBuildFile;
  }

  /**
   * Sets the directory where MSBuild.exe is.
   * 
   * @param dotnetSdkDirectory
   *          the directory
   */
  public void setDotnetSdkDirectory(File dotnetSdkDirectory) {
    this.dotnetSdkDirectory = dotnetSdkDirectory;
  }

  /**
   * Transforms this command object into a array of string that can be passed to the CommandExecutor.
   * 
   * @return the Command that represent the command to launch.
   */
  public Command createCommand() {
    validate();

    LOG.debug("- MSBuild path          : " + dotnetSdkDirectory.getAbsolutePath());
    Command command = Command.create(new File(dotnetSdkDirectory, "MSBuild.exe").getAbsolutePath());

    LOG.debug("- Application Root      : " + fileSystem.getBasedir().getAbsolutePath());
    command.addArgument("/p:AppRoot=" + fileSystem.getBasedir().getAbsolutePath());

    LOG.debug("- Target to run         : StyleCopLaunch");
    command.addArgument("/target:StyleCopLaunch");

    command.addArgument("/noconsolelogger");

    LOG.debug("- MSBuild file          : " + msBuildFile.getAbsolutePath());
    command.addArgument(msBuildFile.getAbsolutePath());

    return command;
  }

  private void validate() {
    if (styleCopConfigFile == null || !styleCopConfigFile.exists()) {
      throw new IllegalStateException("The StyleCop configuration file does not exist.");
    }
    if (msBuildFile == null || !msBuildFile.exists()) {
      throw new IllegalStateException("The MSBuild file does not exist.");
    }
  }
}
