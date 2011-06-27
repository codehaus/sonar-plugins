/*
 * Sonar C# Plugin :: Gallio
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
package org.sonar.plugins.csharp.gallio;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.utils.SonarException;
import org.sonar.dotnet.tools.gallio.GallioCommandBuilder;
import org.sonar.dotnet.tools.gallio.GallioException;
import org.sonar.dotnet.tools.gallio.GallioRunner;
import org.sonar.plugins.csharp.api.CSharpConfiguration;
import org.sonar.plugins.csharp.api.CSharpConstants;
import org.sonar.plugins.csharp.api.MicrosoftWindowsEnvironment;
import org.sonar.plugins.csharp.api.sensor.AbstractCSharpSensor;

/**
 * Executes Gallio to generate test reports.
 */
@DependsUpon(CSharpConstants.CSHARP_CORE_EXECUTED)
@DependedUpon(GallioConstants.GALLIO_EXECUTED)
public class GallioExecutionSensor extends AbstractCSharpSensor {

  private static final Logger LOG = LoggerFactory.getLogger(GallioExecutionSensor.class);

  private static final String GALLIO_EXE = "bin/Gallio.Echo.exe";

  private ProjectFileSystem fileSystem;
  private CSharpConfiguration configuration;
  private String executionMode;

  /**
   * Constructs a {@link GallioExecutionSensor}.
   * 
   * @param fileSystem
   * @param ruleFinder
   * @param fxCopRunner
   * @param profileExporter
   * @param rulesProfile
   */
  public GallioExecutionSensor(ProjectFileSystem fileSystem, CSharpConfiguration configuration,
      MicrosoftWindowsEnvironment microsoftWindowsEnvironment) {
    super(microsoftWindowsEnvironment);
    this.fileSystem = fileSystem;
    this.configuration = configuration;
    this.executionMode = configuration.getString(GallioConstants.MODE, "");
  }

  /**
   * {@inheritDoc}
   */
  public boolean shouldExecuteOnProject(Project project) {
    if (GallioConstants.MODE_SKIP.equalsIgnoreCase(executionMode)) {
      LOG.info("Gallio plugin won't execute as it is set to 'skip' mode.");
      return false;
    }
    if (GallioConstants.MODE_REUSE_REPORT.equals(executionMode)) {
      LOG.info("Gallio plugin won't execute as it is set to 'reuseReport' mode.");
      return false;
    }
    if (getMicrosoftWindowsEnvironment().isTestExecutionDone()) {
      LOG.info("Gallio plugin won't execute as test execution has already been done.");
      return false;
    }

    return super.shouldExecuteOnProject(project);
  }

  @Override
  public void analyse(Project project, SensorContext context) {
    try {
      File gallioExe = new File(configuration.getString(GallioConstants.INSTALL_FOLDER_KEY, GallioConstants.INSTALL_FOLDER_DEFVALUE),
          GALLIO_EXE);
      GallioRunner runner = GallioRunner.create(gallioExe.getAbsolutePath(), false);
      GallioCommandBuilder builder = runner.createCommandBuilder(getMicrosoftWindowsEnvironment().getCurrentSolution());

      String workDir = fileSystem.getSonarWorkingDirectory().getAbsolutePath()
          .substring(fileSystem.getBasedir().getAbsolutePath().length() + 1);
      File reportFile = new File(getMicrosoftWindowsEnvironment().getCurrentSolution().getSolutionDir(), workDir + "/"
          + GallioConstants.GALLIO_REPORT_XML);

      builder.setReportFile(reportFile);
      builder.setFilter(configuration.getString(GallioConstants.FILTER_KEY, GallioConstants.FILTER_DEFVALUE));
      runner.execute(builder, configuration.getInt(GallioConstants.TIMEOUT_MINUTES_KEY, GallioConstants.TIMEOUT_MINUTES_DEFVALUE));
    } catch (GallioException e) {
      throw new SonarException("Gallio execution failed.", e);
    }

    // tell that tests were executed so that no other project tries to launch them a second time
    getMicrosoftWindowsEnvironment().setTestExecutionDone();
  }

}