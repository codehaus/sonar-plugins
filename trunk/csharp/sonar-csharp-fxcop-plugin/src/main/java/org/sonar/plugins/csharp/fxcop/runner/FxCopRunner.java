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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.plugins.csharp.api.CSharpConfiguration;
import org.sonar.plugins.csharp.api.MicrosoftWindowsEnvironment;

/**
 * Class that runs the FxCop program.
 */
public class FxCopRunner implements BatchExtension {

  private static final Logger LOG = LoggerFactory.getLogger(FxCopRunner.class);

  private FxCopCommandBuilder commandBuilder;

  /**
   * Constructs a {@link FxCopRunner}.
   * 
   * @param configuration
   *          FxCop configuration elements
   * @param fileSystem
   *          the file system of the project
   */
  public FxCopRunner(CSharpConfiguration configuration, ProjectFileSystem projectFileSystem,
      MicrosoftWindowsEnvironment microsoftWindowsEnvironment) {
    this.commandBuilder = new FxCopCommandBuilder(configuration, projectFileSystem, microsoftWindowsEnvironment);
  }

  /**
   * Launches the FxCop program.
   * 
   * @param fxCopConfigFile
   *          the FxCop config file to use
   */
  public void execute(File fxCopConfigFile) {
    LOG.debug("Executing FxCop program...");
    commandBuilder.setFxCopConfigFile(fxCopConfigFile);
    // The following no-sonar is used because we can't use TimeUnit#MINUTES (needs Java 1.6+)
    int exitCode = CommandExecutor.create().execute(commandBuilder.createCommand(), commandBuilder.getTimeoutMinutes() * 60000); // NOSONAR
    if (exitCode != 0) {
      throw new SonarException("FxCop execution failed with return code '" + exitCode
          + "'. Check FxCop documentation for more information.");
    }
  }

}
