/*
 * Sonar Standalone Runner
 * Copyright (C) 2011 SonarSource
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
package org.sonar.runner.bootstrapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum BootstrapperVersion {

  INSTANCE;

  private static final String PROPERTIES_PATH = "/org/sonar/runner/version.txt";
  private String version;

  public static String getVersion() {
    return INSTANCE.version;
  }

  private BootstrapperVersion() {
    InputStream input = getClass().getResourceAsStream(PROPERTIES_PATH);
    try {
      Properties properties = new Properties();
      properties.load(input);
      this.version = properties.getProperty("version");

    } catch (IOException e) {
      // Can not load the version
      this.version = "";

    } finally {
      BootstrapperIOUtils.closeQuietly(input);
    }
  }
}
