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

  private Configuration configuration;
  private JaCoCoAgentDownloader downloader;

  public JacocoConfiguration(Configuration configuration, JaCoCoAgentDownloader downloader) {
    this.configuration = configuration;
    this.downloader = downloader;
  }

  public String getReportPath() {
    return configuration.getString(JaCoCoPlugin.REPORT_PATH_PROPERTY, JaCoCoPlugin.REPORT_PATH_DEFAULT_VALUE);
  }

  public String getItReportPath() {
    return configuration.getString(JaCoCoPlugin.IT_REPORT_PATH_PROPERTY);
  }

  public String getJvmArgument() {
    AgentOptions options = new AgentOptions();
    options.setDestfile(getReportPath());
    String includes = configuration.getString(JaCoCoPlugin.INCLUDES_PROPERTY);
    if (StringUtils.isNotBlank(includes)) {
      options.setIncludes(includes);
    }
    String excludes = configuration.getString(JaCoCoPlugin.EXCLUDES_PROPERTY);
    if (StringUtils.isNotBlank(excludes)) {
      options.setExcludes(excludes);
    }
    return options.getVMArgument(downloader.getAgentJarFile());
  }

}
