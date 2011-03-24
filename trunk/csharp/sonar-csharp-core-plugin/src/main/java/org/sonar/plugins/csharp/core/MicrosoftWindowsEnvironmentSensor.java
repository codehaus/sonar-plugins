/*
 * Sonar C# Plugin :: Core
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

package org.sonar.plugins.csharp.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Phase;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.csharp.api.CSharpConstants;
import org.sonar.plugins.csharp.api.MicrosoftWindowsEnvironment;
import org.sonar.plugins.csharp.api.visualstudio.ModelFactory;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioSolution;

/**
 * Sensor used to gather information related to Windows environment (.NET Framework, Visual Studio, ...) and to make it available to other
 * plugins through the {@link MicrosoftWindowsEnvironment} API.
 */
@Phase(name = Phase.Name.PRE)
public class MicrosoftWindowsEnvironmentSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(MicrosoftWindowsEnvironmentSensor.class);

  private ProjectFileSystem projectFileSystem;
  private Configuration configuration;
  private MicrosoftWindowsEnvironment microsoftWindowsEnvironment;

  /**
   * Constructs a {@link MicrosoftWindowsEnvironmentSensor}.
   * 
   * @param configuration
   * @param fileSystem
   */
  public MicrosoftWindowsEnvironmentSensor(Configuration configuration, ProjectFileSystem projectFileSystem,
      MicrosoftWindowsEnvironment microsoftWindowsEnvironment) {
    this.configuration = configuration;
    this.projectFileSystem = projectFileSystem;
    this.microsoftWindowsEnvironment = microsoftWindowsEnvironment;
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
    // First, read all the plugin configuration details related to MS Windows
    retrieveMicrosoftWindowsEnvironmentConfig();

    // Then try to create the Visual Studio Solution object from the ".sln" file
    createVisualStudioSolution();
  }

  private void retrieveMicrosoftWindowsEnvironmentConfig() {
    File dotnetSdkDirectory = new File(configuration.getString(CSharpConstants.DOTNET_SDK_DIR_KEY, CSharpConstants.DOTNET_SDK_DIR_DEFVALUE));
    if ( !dotnetSdkDirectory.isDirectory()) {
      throw new SonarException("The following .NET SDK directory does not exist, please check yoru plugin configuration: "
          + dotnetSdkDirectory.getPath());
    } else {
      microsoftWindowsEnvironment.setDotnetSdkDirectory(dotnetSdkDirectory);
    }
  }

  private void createVisualStudioSolution() {
    File slnFile = findSlnFile();
    if (slnFile == null) {
      throw new SonarException("No valid '.sln' file could be found. Please read the previous log messages to know more.");
    }
    LOG.info("The following 'sln' file has been found and will be used: " + slnFile.getAbsolutePath());

    try {
      VisualStudioSolution solution = ModelFactory.getSolution(slnFile);
      microsoftWindowsEnvironment.setCurrentSolution(solution);
    } catch (IOException e) {
      throw new SonarException("Error occured while reading Visual Studio files.", e);
    }
  }

  private File findSlnFile() {
    String slnFilePath = configuration.getString(CSharpConstants.SOLUTION_FILE_KEY, CSharpConstants.SOLUTION_FILE_DEFVALUE);
    File baseDir = projectFileSystem.getBasedir();
    File slnFile = null;
    if ( !StringUtils.isEmpty(slnFilePath)) {
      slnFile = new File(baseDir, slnFilePath);
      if ( !slnFile.isFile()) {
        LOG.warn("The specified '.sln' path does not point to an existing file: " + slnFile.getAbsolutePath());
        return null;
      }
    }
    if (slnFile == null) {
      LOG.info("No '.sln' file found or specified: trying to find one...");
      slnFile = searchForSlnFile(baseDir);
    }
    return slnFile;
  }

  private File searchForSlnFile(File baseDir) {
    File slnFile = null;
    @SuppressWarnings("unchecked")
    Collection<File> foundSlnFiles = FileUtils.listFiles(baseDir, new String[] { "sln" }, false);
    if (foundSlnFiles.isEmpty()) {
      LOG.warn("No '.sln' file specified, and none found at the root of the project: " + baseDir.getAbsolutePath());
    } else if (foundSlnFiles.size() > 1) {
      LOG.warn("More than one '.sln' file found at the root of the project: please tell which one to use via the configuration ("
          + CSharpConstants.SOLUTION_FILE_KEY + ").");
    } else {
      slnFile = foundSlnFiles.iterator().next();
    }
    return slnFile;
  }
}