/*
 * Sonar C# Plugin :: FxCop
 * Copyright (C) 2010 SonarSource
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

package com.sonar.csharp.fxcop.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.SonarException;

/**
 * Utility class that make it easier to run external tools.
 * 
 * TODO : remove this class in favour of the equivalent one (in "org.sonar.api.utils.command") when upgrading the dependency to Sonar 2.7
 */
public class CommandExecutor {

  /**
   * Executes the external program represented by the command built from the list of strings.
   * 
   * @param command
   *          the list of string that compose the command to launch
   * @param timeoutSeconds
   *          the timeout for the program
   */
  public void execute(List<String> command, long timeoutSeconds) {
    execute(command.toArray(new String[command.size()]), timeoutSeconds);
  }

  /**
   * Executes the external program represented by the command built from the list of strings.
   * 
   * @param command
   *          the list of string that compose the command to launch
   * @param timeoutSeconds
   *          the timeout for the program
   */
  public void execute(String[] command, long timeoutSeconds) {
    ExecutorService executorService = null;
    Process process = null;
    String commandLine = StringUtils.join(command, " ");
    try {
      LoggerFactory.getLogger(getClass()).debug("Executing command: " + commandLine);
      ProcessBuilder builder = new ProcessBuilder(command);
      process = builder.start();

      // consume and display the error and output streams
      StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream());
      StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream());
      outputGobbler.start();
      errorGobbler.start();

      final Process finalProcess = process;
      Callable<Integer> call = new Callable<Integer>() {

        public Integer call() throws Exception { // NOSONAR The "throws Exception" is part of this API
          finalProcess.waitFor();
          return finalProcess.exitValue();
        }
      };

      executorService = Executors.newSingleThreadExecutor();
      Future<Integer> ft = executorService.submit(call);
      int exitVal = ft.get(timeoutSeconds, TimeUnit.SECONDS);

      if (exitVal != 0) {
        throw new SonarException("External program execution failed.");
      }
    } catch (TimeoutException to) {
      if (process != null) {
        process.destroy();
      }
      throw new SonarException("Timeout exceeded: " + timeoutSeconds + " sec., command=" + commandLine, to);

    } catch (InterruptedException e) {
      throw new SonarException("Failed to execute command: " + commandLine, e);

    } catch (ExecutionException e) {
      throw new SonarException("Failed to execute command: " + commandLine, e);

    } catch (IOException e) {
      throw new SonarException("Failed to execute command: " + commandLine, e);

    } finally {
      if (executorService != null) {
        executorService.shutdown();
      }
    }
  }

  static class StreamGobbler extends Thread {

    private InputStream is;

    StreamGobbler(InputStream is) {
      this.is = is;
    }

    @Override
    public void run() {
      Logger logger = LoggerFactory.getLogger(CommandExecutor.class);
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      try {
        String line;
        while ((line = br.readLine()) != null) {
          logger.info(line);
        }
      } catch (IOException ioe) {
        logger.error("Error while reading output", ioe);
      } finally {
        IOUtils.closeQuietly(br);
        IOUtils.closeQuietly(isr);
      }
    }
  }
}
