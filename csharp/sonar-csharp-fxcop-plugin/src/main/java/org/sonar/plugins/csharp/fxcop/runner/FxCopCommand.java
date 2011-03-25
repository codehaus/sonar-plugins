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

package org.sonar.plugins.csharp.fxcop.runner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.csharp.api.MicrosoftWindowsEnvironment;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioProject;
import org.sonar.plugins.csharp.fxcop.FxCopConstants;

import com.google.common.collect.Lists;

/**
 * Class used to build the command line to run FxCop.
 */
public class FxCopCommand {

  private static final Logger LOG = LoggerFactory.getLogger(FxCopCommand.class);

  private ProjectFileSystem fileSystem;
  private MicrosoftWindowsEnvironment microsoftWindowsEnvironment;
  private String fxCopExecutable;
  private String[] assembliesToScan;
  private String[] assemblyDependencyDirectories;
  private boolean ignoreGeneratedCode;
  private int timeoutMinutes = FxCopConstants.TIMEOUT_MINUTES_DEFVALUE;

  private List<File> assemblyToScanFiles;
  private File fxCopConfigFile;
  private File reportFile;

  /**
   * Constructs a {@link FxCopCommand} object.
   * 
   * @param configuration
   *          FxCop configuration elements
   * @param fileSystem
   *          the file system of the project
   */
  public FxCopCommand(Configuration configuration, ProjectFileSystem fileSystem, MicrosoftWindowsEnvironment microsoftWindowsEnvironment) {
    this.fileSystem = fileSystem;
    this.microsoftWindowsEnvironment = microsoftWindowsEnvironment;
    this.fxCopExecutable = configuration.getString(FxCopConstants.EXECUTABLE_KEY, FxCopConstants.EXECUTABLE_DEFVALUE);
    this.assembliesToScan = configuration.getStringArray(FxCopConstants.ASSEMBLIES_TO_SCAN_KEY);
    this.assemblyDependencyDirectories = configuration.getStringArray(FxCopConstants.ASSEMBLY_DEPENDENCY_DIRECTORIES_KEY);
    this.ignoreGeneratedCode = configuration.getBoolean(FxCopConstants.IGNORE_GENERATED_CODE_KEY,
        FxCopConstants.IGNORE_GENERATED_CODE_DEFVALUE);
    this.timeoutMinutes = configuration.getInt(FxCopConstants.TIMEOUT_MINUTES_KEY, FxCopConstants.TIMEOUT_MINUTES_DEFVALUE);
    reportFile = new File(fileSystem.getSonarWorkingDirectory(), FxCopConstants.FXCOP_REPORT_XML);
  }

  /**
   * Sets FxCop configuration file that must be used to perform the analysis. It is mandatory.
   * 
   * @param fxCopConfigFile
   *          the file
   */
  public void setFxCopConfigFile(File fxCopConfigFile) {
    this.fxCopConfigFile = fxCopConfigFile;
  }

  /**
   * Returns the timeout used for the FxCop plugin.
   * 
   * @return the timeout
   */
  public int getTimeoutMinutes() {
    return timeoutMinutes;
  }

  /**
   * Transforms this command object into a array of string that can be passed to the CommandExecutor.
   * 
   * @return the array of strings that represent the command to launch.
   */
  public String[] toArray() {
    assemblyToScanFiles = getAssembliesToScan();
    List<File> assemblyDependencyDirectoriesFiles = getAsListOfFiles(assemblyDependencyDirectories);
    validate();

    List<String> command = new ArrayList<String>();

    LOG.debug("- FxCop program         : " + fxCopExecutable);
    command.add(fxCopExecutable);

    LOG.debug("- Project file          : " + fxCopConfigFile);
    command.add("/p:" + fxCopConfigFile.getAbsolutePath());

    LOG.debug("- Report file           : " + reportFile);
    command.add("/out:" + reportFile.getAbsolutePath());

    LOG.debug("- Scanned assemblies    :");
    for (File checkedAssembly : assemblyToScanFiles) {
      LOG.debug("   o " + checkedAssembly);
      command.add("/f:" + checkedAssembly.getAbsolutePath());
    }

    LOG.debug("- Assembly dependencies :");
    for (File assemblyDependencyDir : assemblyDependencyDirectoriesFiles) {
      LOG.debug("   o " + assemblyDependencyDir);
      command.add("/d:" + assemblyDependencyDir.getAbsolutePath());
    }

    if (ignoreGeneratedCode) {
      LOG.debug("- Ignoring generated code");
      command.add("/igc");
    }

    command.add("/to:" + timeoutMinutes * 60);

    command.add("/gac");

    return command.toArray(new String[command.size()]);
  }

  private List<File> getAssembliesToScan() {
    List<File> assemblyFileList = Lists.newArrayList();
    if (assembliesToScan.length == 0) {
      LOG.debug("No assembly specified: will look into 'csproj' files to find which should be analyzed.");
      assemblyFileList = findAssembliesToScan();
    } else {
      // Some assemblies have been specified: let's analyze them
      assemblyFileList = getAsListOfFiles(assembliesToScan);
    }
    return assemblyFileList;
  }

  private List<File> findAssembliesToScan() {
    List<File> assemblyFileList = Lists.newArrayList();
    for (VisualStudioProject visualStudioProject : microsoftWindowsEnvironment.getCurrentSolution().getProjects()) {
      if ( !visualStudioProject.isTest()) {
        File assembly = visualStudioProject.getDebugArtifact() == null ? visualStudioProject.getReleaseArtifact() : visualStudioProject
            .getDebugArtifact();
        if (assembly != null && assembly.isFile()) {
          LOG.debug(" - Found {}", assembly.getAbsolutePath());
          assemblyFileList.add(assembly);
        }
      }
    }
    return assemblyFileList;
  }

  private List<File> getAsListOfFiles(String[] fileArray) {
    List<File> fileList = Lists.newArrayList();
    File basedir = fileSystem.getBasedir();
    for (int i = 0; i < fileArray.length; i++) {
      String filePath = fileArray[i].trim();
      File file = new File(basedir, filePath);
      if (file == null || !file.exists()) {
        LOG.warn("The following resource can't be found: " + filePath);
      } else {
        fileList.add(file);
      }
    }
    return fileList;
  }

  private void validate() {
    if (fxCopConfigFile == null || !fxCopConfigFile.exists()) {
      throw new IllegalStateException("The FxCop configuration file does not exist.");
    }
    if (assemblyToScanFiles.isEmpty()) {
      throw new IllegalStateException(
          "No assembly to scan. Please check your project's FxCop plugin configuration ('sonar.fxcop.assemblies' property).");
    }
  }
}
