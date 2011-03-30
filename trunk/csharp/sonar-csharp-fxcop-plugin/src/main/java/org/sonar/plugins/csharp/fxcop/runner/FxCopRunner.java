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

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.csharp.api.MicrosoftWindowsEnvironment;

/**
 * Class that runs the FxCop program.
 */
public class FxCopRunner implements BatchExtension {

  private static final Logger LOG = LoggerFactory.getLogger(FxCopRunner.class);

  private FxCopCommand command;

  /**
   * Constructs a {@link FxCopRunner}.
   * 
   * @param configuration
   *          FxCop configuration elements
   * @param fileSystem
   *          the file system of the project
   */
  public FxCopRunner(Configuration configuration, ProjectFileSystem projectFileSystem,
      MicrosoftWindowsEnvironment microsoftWindowsEnvironment) {
    this.command = new FxCopCommand(configuration, projectFileSystem, microsoftWindowsEnvironment);
  }

  /**
   * Launches the FxCop program.
   * 
   * @param fxCopConfigFile
   *          the FxCop config file to use
   */
  public void execute(File fxCopConfigFile) {
    LOG.debug("Executing FxCop program...");
    command.setFxCopConfigFile(fxCopConfigFile);
    new CommandExecutor().execute(command.toArray(), command.getTimeoutMinutes() * 60); // NOSONAR Cannot use TimeUnit#MINUTES (needs Java 1.6+)
  }

}
