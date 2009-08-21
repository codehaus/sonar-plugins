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
package org.sonar.plugins.greenpepper;

import java.io.File;

import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.resources.Project;

public class GreenPepperSensor implements Sensor {

  public void analyse(Project project, SensorContext context) {
    GreenPepperReport testsReport = GreenPepperReportsParser.parseReports(getReportsDirectory(project));

    context.saveMeasure(GreenPepperMetrics.GREENPEPPER_TEST_SUCCESS_DENSITY, testsReport.getTestSuccessPercentage() * 100);
    context.saveMeasure(GreenPepperMetrics.GREENPEPPER_TESTS, (double) testsReport.getTests());
    context.saveMeasure(GreenPepperMetrics.GREENPEPPER_TEST_ERRORS, (double) testsReport.getTestErrors());
    context.saveMeasure(GreenPepperMetrics.GREENPEPPER_TEST_FAILURES, (double) testsReport.getTestFailures());
    context.saveMeasure(GreenPepperMetrics.GREENPEPPER_SKIPPED_TESTS, (double) testsReport.getSkippedTests());
  }

  public boolean shouldExecuteOnProject(Project project) {
    File reportsDirectory = getReportsDirectory(project);
    if (reportsDirectory != null) {
      return true;
    }
    return false;
  }

  private File getReportsDirectory(Project pom) {
    File dir = getReportsDirectoryFromPluginConfiguration(pom);
    if (dir == null) {
      dir = getReportsDirectoryFromDefaultConfiguration(pom);
    }
    if (dir.exists()) {
      return dir;
    }
    return null;
  }

  private File getReportsDirectoryFromPluginConfiguration(Project project) {
    MavenPlugin pomConf = MavenPlugin.getPlugin(project.getPom(), GreenPepperMavenPluginHandler.GROUP_ID,
        GreenPepperMavenPluginHandler.ARTIFACT_ID);
    if (pomConf != null) {
      String path = pomConf.getParameter("reportsDirectory") + "/greenpepper";
      if (path != null) {
        return project.getFileSystem().getFileFromBuildDirectory(path);
      }
    }
    return null;
  }

  private File getReportsDirectoryFromDefaultConfiguration(Project pom) {
    return new File(pom.getFileSystem().getBuildDir(), "greenpepper-reports/greenpepper");
  }

}
