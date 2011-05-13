/*
 * .NET tools :: Gendarme Runner
 * Copyright (C) 2011 Jose Chillan, Alexandre Victoor and SonarSource
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

package org.sonar.dotnet.tools.gendarme;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.command.Command;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioProject;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioSolution;

import com.google.common.collect.Lists;

/**
 * Class used to build the command line to run Gendarme.
 */
public final class GendarmeCommandBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(GendarmeCommandBuilder.class);

  private VisualStudioSolution solution;
  private File gendarmeExecutable;
  private String gendarmeConfidence = "normal+";
  private String gendarmeSeverity = "all";
  private File gendarmeConfigFile;
  private File gendarmeReportFile;

  private GendarmeCommandBuilder() {
  }

  /**
   * Constructs a {@link GendarmeCommandBuilder} object for the given Visual Studio solution.
   */
  public static GendarmeCommandBuilder createBuilder(VisualStudioSolution solution) {
    GendarmeCommandBuilder builder = new GendarmeCommandBuilder();
    builder.solution = solution;
    return builder;
  }

  /**
   * Sets the executable
   * 
   * @param gendarmeExecutable
   *          the executable
   * @return the current builder
   */
  public GendarmeCommandBuilder setExecutable(File gendarmeExecutable) {
    this.gendarmeExecutable = gendarmeExecutable;
    return this;
  }

  /**
   * Sets the configuration file to use
   * 
   * @param gendarmeConfigFile
   *          the config file
   * @return the current builder
   */
  public GendarmeCommandBuilder setConfigFile(File gendarmeConfigFile) {
    this.gendarmeConfigFile = gendarmeConfigFile;
    return this;
  }

  /**
   * Sets the report file to generate
   * 
   * @param reportFile
   *          the report file
   * @return the current builder
   */
  public GendarmeCommandBuilder setReportFile(File reportFile) {
    this.gendarmeReportFile = reportFile;
    return this;
  }

  /**
   * Sets the Gendarme confidence level. By default "normal+" if nothing is specified.
   * 
   * @param gendarmeConfidence
   *          the confidence level
   * @return the current builder
   */
  public GendarmeCommandBuilder setConfidence(String gendarmeConfidence) {
    this.gendarmeConfidence = gendarmeConfidence;
    return this;
  }

  /**
   * Sets the Gendarme severity level. By default "all" if nothing is specified.
   * 
   * @param gendarmeSeverity
   *          the severity level
   * @return the current builder
   */
  public GendarmeCommandBuilder setSeverity(String gendarmeSeverity) {
    this.gendarmeSeverity = gendarmeSeverity;
    return this;
  }

  /**
   * Transforms this command object into a Command object that can be passed to the CommandExecutor.
   * 
   * @return the Command object that represents the command to launch.
   */
  public Command toCommand() {
    List<File> assemblyToScanFiles = findAssembliesToScan(solution);
    validate(assemblyToScanFiles);

    LOG.debug("- Gendarme program    : " + gendarmeExecutable);
    Command command = Command.create(gendarmeExecutable.getAbsolutePath());

    LOG.debug("- Config file         : " + gendarmeConfigFile);
    command.addArgument("--config");
    command.addArgument(gendarmeConfigFile.getAbsolutePath());

    LOG.debug("- Report file         : " + gendarmeReportFile);
    command.addArgument("--xml");
    command.addArgument(gendarmeReportFile.getAbsolutePath());

    LOG.debug("- Quiet output");
    command.addArgument("--quiet");

    LOG.debug("- Confidence          : " + gendarmeConfidence);
    command.addArgument("--confidence");
    command.addArgument(gendarmeConfidence);

    LOG.debug("- Severity            : all");
    command.addArgument("--severity");
    command.addArgument(gendarmeSeverity);

    LOG.debug("- Scanned assemblies  :");
    for (File checkedAssembly : assemblyToScanFiles) {
      LOG.debug("   o " + checkedAssembly);
      command.addArgument(checkedAssembly.getAbsolutePath());
    }

    return command;
  }

  private List<File> findAssembliesToScan(VisualStudioSolution solution) {
    List<File> assemblyFileList = Lists.newArrayList();
    for (VisualStudioProject visualStudioProject : solution.getProjects()) {
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

  private void validate(List<File> assemblyToScanFiles) {
    if (gendarmeConfigFile == null || !gendarmeConfigFile.exists()) {
      throw new IllegalStateException("The Gendarme configuration file does not exist.");
    }
    if (assemblyToScanFiles.isEmpty()) {
      throw new IllegalStateException("No assembly to scan. Please check your project's Gendarme plugin configuration.");
    }
  }
}
