/*
 * Sonar Thucydides Plugin
 * Copyright (C) 2012 OTS SA
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
package org.sonar.plugins.thucydides;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;

public class ThucydidesSensor implements Sensor {

  private ThucydidesResultSiteParser resultsSiteParser;
  private static final Logger LOG = LoggerFactory.getLogger(ThucydidesSensor.class);

  public ThucydidesSensor(ThucydidesResultSiteParser parser) {
    this.resultsSiteParser = parser;
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return project.getAnalysisType().isDynamic(true)
            && Java.KEY.equals(project.getLanguageKey());
  }

  @Override
  public void analyse(Project project, SensorContext context) {

    File reportsPath = project.getFileSystem().resolvePath("target/site/thucydides");
    LOG.debug(reportsPath.getAbsolutePath());

    if (!reportsPath.exists() || !reportsPath.isDirectory()) {
      LOG.warn("Thucidides reports not found in {}", reportsPath);
    } else {
      File[] listOfFiles = reportsPath.listFiles(new XmlFileFilter());
      for (File file : listOfFiles) {
        LOG.debug(file.getName());
        resultsSiteParser.parseThucydidesReportFile(reportsPath);
      }
    }

  }
}
