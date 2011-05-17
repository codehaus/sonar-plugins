package com.echosource.ada.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.utils.SonarException;

import com.echosource.ada.gnat.metric.GnatConfiguration;

/**
 * Abstract plugin executor. This class handles common executor needs such as running the process, reading its common and error output
 * streams and logging. In nominal case implementing executor should just construct the desire command line.
 */
public abstract class PluginAbstractExecutor implements BatchExtension {

  /** The logger */
  private static final Logger LOG = LoggerFactory.getLogger(PluginAbstractExecutor.class);

  /**
   * The Class AsyncPipe.
   */
  static class AsyncPipe extends Thread {

    private static final int BUFFER_SIZE = 1024;

    /** The input stream. */
    private InputStream inputStream;

    /** The output stream. */
    private OutputStream outputStream;

    /**
     * Instantiates a new async pipe.
     * 
     * @param input
     *          an InputStream
     * @param output
     *          an OutputStream
     */
    public AsyncPipe(InputStream input, OutputStream output) {
      inputStream = input;
      outputStream = output;
    }

    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
      try {
        final byte[] buffer = new byte[BUFFER_SIZE];
        // Reads the process input stream and writes it to the output stream
        int length = inputStream.read(buffer);
        while (length != -1) {
          synchronized (outputStream) {
            outputStream.write(buffer, 0, length);
          }
          length = inputStream.read(buffer);
        }
      } catch (IOException e) {
        LOG.error("Can't execute the Async Pipe", e);
      }
    }
  }

  /**
   * Executes the external tool.
   */
  public void execute() {
    if ( !getConfiguration().isAnalyzeOnly()) {
      // Gets the tool command line
      try {
        List<String> commandLine = getCommandLine();
        LOG.info("Executing " + getExecutable() + " with command '{}'", prettyPrint(commandLine));
        ProcessBuilder builder = new ProcessBuilder(commandLine);
        // Starts the process
        Process process = builder.start();
        // And handles it's normal and error stream in separated threads.
        new AsyncPipe(process.getInputStream(), System.out).start();
        new AsyncPipe(process.getErrorStream(), System.err).start();
        LOG.info(getExecutable() + " ended with returned code '{}'.", process.waitFor());
      } catch (IOException e) {
        LOG.error("Can't execute the external tool", e);
        throw new SonarException(e);
      } catch (InterruptedException e) {
        LOG.error("Async pipe interrupted: ", e);
      }
    }
  }

  protected abstract GnatConfiguration getConfiguration();

  /**
   * Returns a String where each list member is separated with a space
   * 
   * @param commandLine
   *          the external tool command line argument
   * @return String where each list member is separated with a space
   */
  private String prettyPrint(List<String> commandLine) {
    StringBuilder sb = new StringBuilder();
    for (Iterator<String> iter = commandLine.iterator(); iter.hasNext();) {
      String part = iter.next();
      sb.append(part);
      if (iter.hasNext()) {
        sb.append(" ");
      }
    }
    return sb.toString();
  }

  /**
   * Gets the command line.
   * 
   * @return the command line
   */
  protected abstract List<String> getCommandLine();

  /**
   * Gets the executed tool.
   * 
   * @return the executed tool
   */
  protected abstract String getExecutable();
}