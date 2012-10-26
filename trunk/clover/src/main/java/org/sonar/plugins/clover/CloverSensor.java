/*
 * Sonar Clover Plugin
 * Copyright (C) 2008 SonarSource
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

package org.sonar.plugins.clover;

import java.io.File;

import org.slf4j.LoggerFactory;
import org.sonar.api.batch.CoverageExtension;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;

public class CloverSensor implements Sensor, CoverageExtension {

  private CloverSettings settings;

  public CloverSensor(CloverSettings settings) {
    this.settings = settings;
  }

  public boolean shouldExecuteOnProject(Project project) {
    return settings.isEnabled()
      && !project.getFileSystem().mainFiles(Java.KEY).isEmpty()
      && project.getAnalysisType().isDynamic(true);
  }

  public void analyse(Project project, SensorContext context) {
    File report = getReportFromProperty(project);
    if (reportExists(report)) {
      new XmlReportParser(context).collect(report);
    } else {
      LoggerFactory.getLogger(getClass()).info("Clover XML report not found");
    }
  }

  private File getReportFromProperty(Project project) {
    String path = (String) project.getProperty(CloverConstants.REPORT_PATH_PROPERTY);
    if (path != null) {
      return project.getFileSystem().resolvePath(path);
    }
    return null;
  }

  private boolean reportExists(File report) {
    return report != null && report.exists() && report.isFile();
  }

}
