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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.Plugins;
import org.sonar.api.batch.AbstractCoverageExtension;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.maven.DependsUponMavenPlugin;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.resources.Project;

import java.io.File;

public class EmmaSensor extends AbstractCoverageExtension implements Sensor, DependsUponMavenPlugin {
  private EmmaMavenPluginHandler pluginHandler;
  Logger logger = LoggerFactory.getLogger(getClass());


  public EmmaSensor(Plugins plugins, EmmaMavenPluginHandler pluginHandler) {
    super(plugins);
    this.pluginHandler = pluginHandler;
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return super.shouldExecuteOnProject(project) && project.getFileSystem().hasJavaSourceFiles();
  }

  public void analyse(Project project, SensorContext context) {
    File reportsPath = getReport(project);
    EmmaProcessor processor = new EmmaProcessor(reportsPath, context);
    processor.process();
  }

  public MavenPluginHandler getMavenPluginHandler(Project project) {
    if (project.getAnalysisType().equals(Project.AnalysisType.DYNAMIC)) {
      return pluginHandler;
    }
    return null;
  }


  protected File getReport(Project project) {
    File report = getReportFromProperty(project);
    if (report == null) {
      report = getReportFromPluginConfiguration(project);
    }
    if (report == null) {
      report = getReportFromDefaultPath(project);
    }

    if (report == null || !report.exists() || !report.isDirectory()) {
      logger.warn("Emma report path not found at {}", report);
      report = null;
    }
    return report;
  }

  private File getReportFromProperty(Project project) {
    String path = (String) project.getProperty(EmmaPlugin.REPORT_PATH_PROPERTY);
    if (path != null) {
      return project.getFileSystem().resolvePath(path);
    }
    return null;
  }

  private File getReportFromPluginConfiguration(Project project) {
    MavenPlugin mavenPlugin = MavenPlugin.getPlugin(project.getPom(), EmmaMavenPluginHandler.GROUP_ID, EmmaMavenPluginHandler.ARTIFACT_ID);
    if (mavenPlugin != null) {
      String path = mavenPlugin.getParameter("outputDirectory");
      if (path != null) {
        return project.getFileSystem().resolvePath(path);
      }
    }
    return null;
  }

  private File getReportFromDefaultPath(Project project) {
    return project.getFileSystem().getBuildDir();
  }

}
