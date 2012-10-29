/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
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
package org.sonar.plugins.emma;

import org.apache.commons.lang.ArrayUtils;
import org.sonar.api.BatchExtension;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;

public class EmmaSettings implements BatchExtension {

  private Settings settings;

  public EmmaSettings(Settings settings) {
    this.settings = settings;
  }

  public boolean isEnabled(Project project) {
    if (project.getAnalysisType().isDynamic(true) && !project.getFileSystem().mainFiles(EmmaPlugin.JAVA_LANGUAGE_KEY).isEmpty()) {
      // backward-compatibility with the property that has been deprecated in sonar 3.4.
      String[] keys = settings.getStringArray("sonar.core.codeCoveragePlugin");
      if (keys.length > 0) {
        return ArrayUtils.contains(keys, EmmaPlugin.PLUGIN_KEY);
      }

      // should use org.sonar.plugins.java.api.JavaSettings#getEnabledCoveragePlugin() introduced in sonar-java-plugin 1.1 with sonar 3.4
      return EmmaPlugin.PLUGIN_KEY.equals(settings.getString("sonar.java.coveragePlugin"));
    }
    return false;
  }

  public String getReportPath() {
    return settings.getString(EmmaPlugin.REPORT_PATH_PROPERTY);
  }

  public EmmaSettings setReportPath(String s) {
    settings.setProperty(EmmaPlugin.REPORT_PATH_PROPERTY, s);
    return this;
  }
}
