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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.ProjectFileSystem;

/**
 * Class used to build the command line to run StyleCop.
 */
public class StyleCopCommand {

  private static final Logger LOG = LoggerFactory.getLogger(StyleCopCommand.class);

  private ProjectFileSystem fileSystem;
  private File styleCopConfigFile;
  private File msBuildFile;
  private File dotnetSdkDirectory;

  /**
   * Constructs a {@link StyleCopCommand} object.
   * 
   * @param fileSystem
   *          the file system of the project
   */
  public StyleCopCommand(ProjectFileSystem fileSystem) {
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
   * @return the array of strings that represent the command to launch.
   */
  public String[] toArray() {
    validate();

    List<String> command = new ArrayList<String>();

    LOG.debug("- MSBuild path          : " + dotnetSdkDirectory.getAbsolutePath());
    command.add(new File(dotnetSdkDirectory, "MSBuild.exe").getAbsolutePath());

    LOG.debug("- Application Root      : " + fileSystem.getBasedir().getAbsolutePath());
    command.add("/p:AppRoot=" + fileSystem.getBasedir().getAbsolutePath());

    LOG.debug("- Target to run         : StyleCopLaunch");
    command.add("/target:StyleCopLaunch");

    command.add("/noconsolelogger");

    LOG.debug("- MSBuild file          : " + msBuildFile.getAbsolutePath());
    command.add(msBuildFile.getAbsolutePath());

    return command.toArray(new String[command.size()]);
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
