/*
 * Sonar JaCoCo Plugin
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

package org.sonar.plugins.jacoco;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.jacoco.core.runtime.AgentOptions;
import org.sonar.api.BatchExtension;

public class JacocoConfiguration implements BatchExtension {

  public static final String REPORT_PATH_PROPERTY = "sonar.jacoco.reportPath";
  public static final String REPORT_PATH_DEFAULT_VALUE = "target/jacoco.exec";
  public static final String IT_REPORT_PATH_PROPERTY = "sonar.jacoco.itReportPath";
  public static final String IT_REPORT_PATH_DEFAULT_VALUE = "";
  public static final String INCLUDES_PROPERTY = "sonar.jacoco.includes";
  public static final String EXCLUDES_PROPERTY = "sonar.jacoco.excludes";
  public static final String ANT_TARGETS_PROPERTY = "sonar.jacoco.antTargets";
  public static final String ANT_TARGETS_DEFAULT_VALUE = "";

  private Configuration configuration;
  private JaCoCoAgentDownloader downloader;

  public JacocoConfiguration(Configuration configuration, JaCoCoAgentDownloader downloader) {
    this.configuration = configuration;
    this.downloader = downloader;
  }

  public String getReportPath() {
    return configuration.getString(REPORT_PATH_PROPERTY, REPORT_PATH_DEFAULT_VALUE);
  }

  public String getItReportPath() {
    return configuration.getString(IT_REPORT_PATH_PROPERTY, IT_REPORT_PATH_DEFAULT_VALUE);
  }

  public String getJvmArgument() {
    AgentOptions options = new AgentOptions();
    options.setDestfile(getReportPath());
    String includes = configuration.getString(INCLUDES_PROPERTY);
    if (StringUtils.isNotBlank(includes)) {
      options.setIncludes(includes);
    }
    String excludes = configuration.getString(EXCLUDES_PROPERTY);
    if (StringUtils.isNotBlank(excludes)) {
      options.setExcludes(excludes);
    }
    return options.getVMArgument(downloader.getAgentJarFile());
  }

  public String[] getAntTargets() {
    return StringUtils.split(configuration.getString(ANT_TARGETS_PROPERTY, ANT_TARGETS_DEFAULT_VALUE), ',');
  }

}
