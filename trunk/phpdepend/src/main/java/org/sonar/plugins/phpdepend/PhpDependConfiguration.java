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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.sonar.plugins.api.maven.model.MavenPom;

import java.io.File;
import java.io.IOException;

public class PhpDependConfiguration {

  private MavenPom pom = null;
  protected static final String KEY_PATH = "phpdepend.path";
  protected static final String DEFAUT_PATH = "";

  private static final String PHPDEPEND_COMMAND = "pdepend";
  protected static final String REPORT_DIR = "target";
  private static final String SUMMARY_FILE = "phpdepend-summary.xml";
  private static final String SUMMARY_OPT = "--summary-xml="+ REPORT_DIR + SUMMARY_FILE;

  public PhpDependConfiguration(MavenPom pom) {
    this.pom = pom;
    init();
  }

  // Only for unit tests
  protected PhpDependConfiguration(){
  }

  protected void init(){
    try {
      File reportDir = new File(getBuildDir(), REPORT_DIR);
      FileUtils.forceMkdir(reportDir);
    } catch (IOException e) {
      throw new PhpDependExecutionException(e);
    }
  }

  public String getCommandLine() {
    String path = getPath();
    if (isOsWindows()) {
      return path + PHPDEPEND_COMMAND + ".bat";
    } else {
      if (StringUtils.isEmpty(path)) {
        return PHPDEPEND_COMMAND;
      } else {
        path = StringUtils.removeEnd(path, "/");
        return path + "/" + PHPDEPEND_COMMAND;
      }
    }
  }

  public String getSummaryOption() {
    return SUMMARY_OPT;
  }

  public String getPath() {
    return pom.getConfiguration().getString(KEY_PATH, DEFAUT_PATH);
  }

  protected boolean isOsWindows(){
    return SystemUtils.IS_OS_WINDOWS;
  }

  protected File getBuildDir(){
    return pom.getBuildDir();
  }

}
