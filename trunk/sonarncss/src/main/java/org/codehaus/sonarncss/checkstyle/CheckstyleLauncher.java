/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.codehaus.sonarncss.checkstyle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.codehaus.sonarncss.SonarNcss;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.DefaultLogger;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.Configuration;

/**
 * JavaNCSS uses Checkstyle to get an out-of-the-box java parser with AST
 * generation and visitor pattern support.
 *
 * @author Freddy Mallet
 */
public class CheckstyleLauncher {

  private static String CHECKSTYLE_CONF_FILE_NAME = "checkstyle-configuration.xml";

  /**
   * Create and execute the Checkstyle engine.
   *
   * @param files        list of files to analyse. This list shouldn't contain any
   *                     directory.
   * @param confFileName the Checkstyle configuration file name to load.
   */
  public static void launchCheckstyleEngine(List<File> files) {
    Checker c = createChecker(CHECKSTYLE_CONF_FILE_NAME);

    File[] processedFiles = new File[files.size()];
    files.toArray(processedFiles);

    c.process(processedFiles);
    c.destroy();
  }

  /**
   * Creates the Checkstyle Checker object.
   *
   * @param confFileName the name of the Checkstyle configuration file which should be
   *                     available accessible the ClassLoader.
   * @return a nice new fresh Checkstyle Checker
   */
  private static Checker createChecker(String confFileName) {
    try {
      
      InputStream checkstyleConfig = CheckstyleLauncher.class.getClassLoader().getResourceAsStream(confFileName);
      Configuration config = ConfigurationLoader.loadConfiguration(checkstyleConfig, new PropertiesExpander(System.getProperties()), false);
      Checker c = new Checker();
      c.configure(config);
      
      final Logger logger = LoggerFactory.getLogger(SonarNcss.class);
      
      StreamLogger infoLogger = new StreamLogger() {
        @Override
        public void log(String log) {
          logger.info(log);
        }};
      StreamLogger errorLogger = new StreamLogger() {
        @Override
        public void log(String log) {
          logger.error(log);
        }};

      c.addListener(new DefaultLogger(infoLogger, true, errorLogger, true));
      return c;
    } catch (final Exception e) {
      throw new RuntimeException("Unable to create Checkstyle Checker object with '" + confFileName
          + "' as Checkstyle configuration file name", e);
    }
  }
  
  private abstract static class StreamLogger extends OutputStream {
    
    private StringBuilder buffer = new StringBuilder(256);

    @Override
    public void write(int byteToWrite) throws IOException {
      char character = (char)byteToWrite;
      if (character == '\n') {
        logAndResetBuffer();
      } else {
        buffer.append(character);
      }
    }

    private void logAndResetBuffer() {
      log(buffer.toString().trim());
      buffer.setLength(0);
    }
    
    public abstract void log(String log);

    @Override
    public void close() throws IOException {
      if (buffer.length() > 0) {
        logAndResetBuffer(); 
      }
      super.close();
    }
  }
  

}
