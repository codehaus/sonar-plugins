/*
 * Sonar C# Plugin :: Gendarme
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

package org.sonar.plugins.csharp.gendarme.runner;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.csharp.api.MicrosoftWindowsEnvironment;
import org.sonar.plugins.csharp.api.utils.Command;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioProject;
import org.sonar.plugins.csharp.gendarme.GendarmeConstants;

import com.google.common.collect.Lists;

/**
 * Class used to build the command line to run Gendarme.
 */
public class GendarmeCommandBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(GendarmeCommandBuilder.class);
  private static final String GENDARME_EXECUTABLE = "gendarme.exe";

  private ProjectFileSystem fileSystem;
  private MicrosoftWindowsEnvironment microsoftWindowsEnvironment;
  private String gendarmeInstallDir;
  private String[] assembliesToScan;
  private String gendarmeConfidence;
  private List<File> assemblyToScanFiles;
  private File gendarmeConfigFile;
  private File reportFile;

  /**
   * Constructs a {@link GendarmeCommandBuilder} object.
   * 
   * @param configuration
   *          Gendarme configuration elements
   * @param fileSystem
   *          the file system of the project
   */
  public GendarmeCommandBuilder(Configuration configuration, ProjectFileSystem fileSystem,
      MicrosoftWindowsEnvironment microsoftWindowsEnvironment) {
    this.fileSystem = fileSystem;
    this.microsoftWindowsEnvironment = microsoftWindowsEnvironment;
    this.gendarmeInstallDir = configuration.getString(GendarmeConstants.INSTALL_DIR_KEY, GendarmeConstants.INSTALL_DIR_DEFVALUE);
    this.assembliesToScan = configuration.getStringArray(GendarmeConstants.ASSEMBLIES_TO_SCAN_KEY);
    this.gendarmeConfidence = configuration.getString(GendarmeConstants.GENDARME_CONFIDENCE_KEY,
        GendarmeConstants.GENDARME_CONFIDENCE_DEFVALUE);
    reportFile = new File(fileSystem.getSonarWorkingDirectory(), GendarmeConstants.GENDARME_REPORT_XML);
  }

  /**
   * Sets Gendarme configuration file that must be used to perform the analysis. It is mandatory.
   * 
   * @param gendarmeConfigFile
   *          the file
   */
  public void setGendarmeConfigFile(File gendarmeConfigFile) {
    this.gendarmeConfigFile = gendarmeConfigFile;
  }

  /**
   * Transforms this command object into a array of string that can be passed to the CommandExecutor.
   * 
   * @return the Command that represent the command to launch.
   */
  public Command createCommand() {
    assemblyToScanFiles = getAssembliesToScan();
    validate();

    File gendarmeExecutable = new File(new File(gendarmeInstallDir), GENDARME_EXECUTABLE);
    LOG.debug("- Gendarme program    : " + gendarmeExecutable);
    Command command = Command.create(gendarmeExecutable.getAbsolutePath());

    LOG.debug("- Config file         : " + gendarmeConfigFile);
    command.addArgument("--config");
    command.addArgument(gendarmeConfigFile.getAbsolutePath());

    LOG.debug("- Report file         : " + reportFile);
    command.addArgument("--xml");
    command.addArgument(reportFile.getAbsolutePath());

    LOG.debug("- Quiet output");
    command.addArgument("--quiet");

    LOG.debug("- Confidence          : " + gendarmeConfidence);
    command.addArgument("--confidence");
    command.addArgument(gendarmeConfidence);

    LOG.debug("- Severity            : all");
    command.addArgument("--severity");
    command.addArgument("all");

    LOG.debug("- Scanned assemblies  :");
    for (File checkedAssembly : assemblyToScanFiles) {
      LOG.debug("   o " + checkedAssembly);
      command.addArgument(checkedAssembly.getAbsolutePath());
    }

    return command;
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
    if (gendarmeConfigFile == null || !gendarmeConfigFile.exists()) {
      throw new IllegalStateException("The Gendarme configuration file does not exist.");
    }
    if (assemblyToScanFiles.isEmpty()) {
      throw new IllegalStateException(
          "No assembly to scan. Please check your project's Gendarme plugin configuration ('sonar.gendarme.assemblies' property).");
    }
  }
}
