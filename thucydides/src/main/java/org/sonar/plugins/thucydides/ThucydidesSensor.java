/*
 * Sonar Thucydides Plugin
 * Copyright (C) 2012 Patroklos PAPAPETROU
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

public class ThucydidesSensor implements Sensor {

  private ThucydidesResultSiteParser resultsSiteParser;
  private static final Logger LOG = LoggerFactory.getLogger(ThucydidesSensor.class);

  public ThucydidesSensor(ThucydidesResultSiteParser parser) {
    this.resultsSiteParser = parser;
  }

  public boolean shouldExecuteOnProject(Project project) {
    System.out.println ( Java.KEY);
    System.out.println ( project.getLanguageKey());
    return Java.KEY.equals(project.getLanguageKey());
  }

  public void analyse(Project project, SensorContext context) {

    File reportsPath = project.getFileSystem().resolvePath("target/site/thucydides");

    if (reportsPath.exists() && reportsPath.isDirectory()) {
      final ThucydidesReport thucydidesReport = resultsSiteParser.parseThucydidesReports(reportsPath);
      context.saveMeasure(ThucydidesMetrics.THUCYDIDES_TESTS, (double) thucydidesReport.getTests());
      context.saveMeasure(ThucydidesMetrics.THUCYDIDES_TESTS_FAILED, (double) thucydidesReport.getFailed());
      context.saveMeasure(ThucydidesMetrics.THUCYDIDES_TESTS_PASSED, (double) thucydidesReport.getPassed());
      context.saveMeasure(ThucydidesMetrics.THUCYDIDES_TESTS_PENDING, (double) thucydidesReport.getPending());
      context.saveMeasure(ThucydidesMetrics.THUCYDIDES_TESTS_DURATION, (double) thucydidesReport.getDuration());
      context.saveMeasure(ThucydidesMetrics.THUCYDIDES_SUCCESS_DENSITY, (double) thucydidesReport.getSuccesRate());
      context.saveMeasure(ThucydidesMetrics.THUCYDIDES_STORIES, (double) thucydidesReport.getStoriesCount());
      context.saveMeasure(ThucydidesMetrics.THUCYDIDES_FEATURES, (double) thucydidesReport.getFeaturesCount());
    } else {
      LOG.warn("Thucydides reports not found in {}", reportsPath);
    }
  }
}
