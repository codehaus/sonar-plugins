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

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.csharp.api.MicrosoftWindowsEnvironment;
import org.sonar.plugins.csharp.api.utils.CommandExecutor;
import org.sonar.plugins.csharp.gendarme.GendarmeConstants;

/**
 * Class that runs the Gendarme program.
 */
public class GendarmeRunner implements BatchExtension {

  private static final Logger LOG = LoggerFactory.getLogger(GendarmeRunner.class);

  private GendarmeCommandBuilder commandBuilder;
  private int timeoutMinutes;

  /**
   * Constructs a {@link GendarmeRunner}.
   * 
   * @param configuration
   *          Gendarme configuration elements
   * @param fileSystem
   *          the file system of the project
   */
  public GendarmeRunner(Configuration configuration, ProjectFileSystem projectFileSystem,
      MicrosoftWindowsEnvironment microsoftWindowsEnvironment) {
    this.commandBuilder = new GendarmeCommandBuilder(configuration, projectFileSystem, microsoftWindowsEnvironment);
    timeoutMinutes = configuration.getInt(GendarmeConstants.TIMEOUT_MINUTES_KEY, GendarmeConstants.TIMEOUT_MINUTES_DEFVALUE);
  }

  /**
   * Launches the Gendarme program.
   * 
   * @param gendarmeConfigFile
   *          the Gendarme config file to use
   */
  public void execute(File gendarmeConfigFile) {
    LOG.debug("Executing Gendarme program...");
    commandBuilder.setGendarmeConfigFile(gendarmeConfigFile);
    // The following no-sonar is used because we can't use TimeUnit#MINUTES (needs Java 1.6+)
    int exitCode = CommandExecutor.create().execute(commandBuilder.createCommand(), timeoutMinutes * 60000); // NOSONAR
    // Gendarme returns 1 when the analysis is successful but contains violations, so 1 is valid
    if (exitCode != 0 && exitCode != 1) {
      throw new SonarException("Gendarme execution failed. Check the logs for more detail.");
    }
  }

}
