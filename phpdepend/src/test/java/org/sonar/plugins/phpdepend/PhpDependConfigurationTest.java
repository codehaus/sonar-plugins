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

package org.sonar.plugins.phpdepend;

import org.apache.commons.io.FileUtils;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class PhpDependConfigurationTest {

  private final File targetDir = new File("target");
  private File reportDir = new File(targetDir, PhpDependConfiguration.REPORT_DIR);

  @Before
  public void before() throws IOException {
    FileUtils.deleteQuietly(reportDir);
  }

  @Test
  public void shouldGetCommandLineForWindows() {
    PhpDependConfiguration config = getWindowsConfiguration();
    assertThat(config.getCommandLine(), is("pdepend.bat"));
  }

  @Test
  public void shouldGetCommandLineForNotWindows() {
    PhpDependConfiguration config = getNotWindowsConfiguration();
    assertThat(config.getCommandLine(), is("pdepend"));
  }

  @Test
  public void shouldGetCommandLineWithPath() {
    String path = "path/to/phpdepend";
    PhpDependConfiguration config = getConfiguration(false, path);
    assertThat(config.getCommandLine(), is(path + "/pdepend"));
  }

  @Test
  public void shouldGetCommandLineWithPathEvenIfExistingLastSlash() {
    String path = "path/to/phpdepend";
    PhpDependConfiguration config = getConfiguration(false, path + "/");
    assertThat(config.getCommandLine(), is(path + "/pdepend"));
  }

  @Test
  public void shouldCreateReportDir() throws IOException {
    PhpDependConfiguration config = new PhpDependConfiguration() {
      protected File getBuildDir() {
        return targetDir;
      }
    };
    assertFalse(reportDir.exists());
    config.init();
    assertTrue(reportDir.exists());
  }


  private PhpDependConfiguration getWindowsConfiguration() {
    return getConfiguration(true, "");
  }

  private PhpDependConfiguration getNotWindowsConfiguration() {
    return getConfiguration(false, "");
  }

  private PhpDependConfiguration getConfiguration(final boolean isOsWindows, final String path) {
    PhpDependConfiguration config = new PhpDependConfiguration() {
      public String getPath() {
        return path;
      }

      protected boolean isOsWindows() {
        return isOsWindows;
      }
    };
    return config;
  }

}
