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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.CoverageExtension;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;

import java.io.File;

public class EmmaSensor implements Sensor, CoverageExtension {

  private final static Logger LOGGER = LoggerFactory.getLogger(EmmaSensor.class);

  private EmmaSettings settings;

  public EmmaSensor(EmmaSettings settings) {
    this.settings = settings;
  }

  public boolean shouldExecuteOnProject(Project project) {
    return settings.isEnabled(project);
  }

  public void analyse(Project project, SensorContext context) {
    String path = settings.getReportPath();
    if (StringUtils.isEmpty(path)) {
      // wasn't configured - skip
      return;
    }
    File reportsPath = project.getFileSystem().resolvePath(path);
    if (!reportsPath.exists() || !reportsPath.isDirectory()) {
      LOGGER.warn("Emma reports not found in {}", reportsPath);
      return;
    }

    LOGGER.info("Parse reports: " + reportsPath);
    EmmaProcessor processor = new EmmaProcessor(reportsPath, context);
    processor.process();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
