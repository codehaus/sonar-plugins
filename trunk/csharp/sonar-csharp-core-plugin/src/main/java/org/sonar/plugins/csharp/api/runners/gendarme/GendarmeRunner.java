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

package org.sonar.plugins.csharp.api.runners.gendarme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;

/**
 * Class that runs the Gendarme program.
 */
public class GendarmeRunner {

  private static final Logger LOG = LoggerFactory.getLogger(GendarmeRunner.class);

  private static final int MINUTES_TO_MILLISECONDS = 60000;

  /**
   * Creates a new {@link GendarmeRunner} object.
   */
  public GendarmeRunner() {
  }

  /**
   * Executes the given Gendarme command.
   * 
   * @param command
   *          the command
   * @param timeoutMinutes
   *          the timeout for the command
   * @throws GendarmeException
   *           if Gendarme fails to execute
   */
  public void execute(Command command, int timeoutMinutes) throws GendarmeException {
    // TODO: check if executable exists, and if not, unzip the Gendarme executable

    LOG.debug("Executing Gendarme program...");

    int exitCode = CommandExecutor.create().execute(command, timeoutMinutes * MINUTES_TO_MILLISECONDS);
    // Gendarme returns 1 when the analysis is successful but contains violations, so 1 is valid
    if (exitCode != 0 && exitCode != 1) {
      throw new GendarmeException(exitCode);
    }
  }

}
