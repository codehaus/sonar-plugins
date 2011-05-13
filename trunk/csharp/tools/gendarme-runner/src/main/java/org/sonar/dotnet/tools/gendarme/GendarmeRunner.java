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
import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.dotnet.tools.commons.utils.ZipUtils;
import org.sonar.plugins.csharp.api.visualstudio.VisualStudioSolution;

/**
 * Class that runs the Gendarme program.
 */
public class GendarmeRunner { // NOSONAR : can't mock it otherwise

  private static final Logger LOG = LoggerFactory.getLogger(GendarmeRunner.class);

  private static final int MINUTES_TO_MILLISECONDS = 60000;
  private static final String EMBEDDED_VERSION = "2.10";

  private File gendarmeExecutable;

  private GendarmeRunner() {
  }

  /**
   * Creates a new {@link GendarmeRunner} object for the given executable file. If the executable file does not exist, then the embedded one
   * will be used.
   * 
   * @param defaultExecutablePath
   *          the full path of the executable. For instance: "C:/Program Files/gendarme-2.10-bin/gendarme.exe"
   * @param tempFolder
   *          the temporary folder where the embeded Gendarme executable will be copied if the gendarmePath does not point to a valid
   *          executable
   */
  public static GendarmeRunner create(String gendarmePath, String tempFolder) throws GendarmeException {
    GendarmeRunner runner = new GendarmeRunner();

    File executable = new File(gendarmePath);
    runner.gendarmeExecutable = executable;
    if ( !executable.exists() || !executable.isFile()) {
      LOG.info("Gendarme executable not found: '{}'. The embedded version ({}) will be used instead.", executable.getAbsolutePath(),
          EMBEDDED_VERSION);
      try {
        URL executableURL = GendarmeRunner.class.getResource("/gendarme-" + EMBEDDED_VERSION + "-bin");
        File extractedFolder = ZipUtils.extractArchiveFolderIntoDirectory(StringUtils.substringBefore(executableURL.getFile(), "!")
            .substring(5), "gendarme-2.10-bin", tempFolder);
        runner.gendarmeExecutable = new File(extractedFolder, "gendarme.exe");
      } catch (IOException e) {
        throw new GendarmeException("Could not extract the embedded Gendarme executable: " + e.getMessage(), e);
      }
    }

    return runner;
  }

  /**
   * Creates an empty {@link GendarmeCommandBuilder}, containing only the path of the Gendarme executable.
   * 
   * @return the command to complete before running the {@link #execute(Command, int)} method.
   */
  public GendarmeCommandBuilder createCommandBuilder(VisualStudioSolution solution) {
    GendarmeCommandBuilder builder = GendarmeCommandBuilder.createBuilder(solution);
    builder.setExecutable(gendarmeExecutable);
    return builder;
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
    LOG.debug("Executing Gendarme program...");
    int exitCode = CommandExecutor.create().execute(command, timeoutMinutes * MINUTES_TO_MILLISECONDS);
    // Gendarme returns 1 when the analysis is successful but contains violations, so 1 is valid
    if (exitCode != 0 && exitCode != 1) {
      throw new GendarmeException(exitCode);
    }
  }

}
