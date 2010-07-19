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
  public static final String PROP_REPORT_PATH = "sonar.emma.reportPath";

  private EmmaMavenPluginHandler pluginHandler;

  public EmmaSensor(Plugins plugins, EmmaMavenPluginHandler pluginHandler) {
    super(plugins);
    this.pluginHandler = pluginHandler;
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return super.shouldExecuteOnProject(project) && project.getFileSystem().hasJavaSourceFiles();
  }

  public MavenPluginHandler getMavenPluginHandler(Project project) {
    if (project.getAnalysisType().equals(Project.AnalysisType.DYNAMIC)) {
      return pluginHandler;
    }
    // do not execute maven plugin if reuseReport mode
    return null;
  }

  public void analyse(Project project, SensorContext context) {
    File report = getReport(project);
    if (checkReportAvailability(report)) {
      EmmaXmlProcessor emmaXmlProcessor = new EmmaXmlProcessor(report, context);
      emmaXmlProcessor.process();
      // TODO: Hack for SONARPLUGINS-52
      EmmaProcessor processor = new EmmaProcessor(project.getFileSystem().getBuildDir(), context);
      processor.process();
    }
  }


  private boolean checkReportAvailability(File report) {
    Logger logger = LoggerFactory.getLogger(getClass());
    if (report == null || !report.exists() || !report.isFile()) {
      logger.error("Emma report not found : {}. Project coverage is set to 0%.", report);
      return false;
    }

    logger.info("Analysing {}", report.getAbsolutePath());
    return true;
  }

  private File getReport(Project project) {
    File report = getReportFromProperty(project);
    if (report == null) {
      report = getReportFromPluginConfiguration(project);
    }
    if (report == null) {
      report = getReportFromDefaultPath(project);
    }
    return report;
  }

  private File getReportFromProperty(Project project) {
    String path = (String) project.getProperty(PROP_REPORT_PATH);
    if (path != null) {
      return project.getFileSystem().resolvePath(path);
    }
    return null;
  }

  private File getReportFromPluginConfiguration(Project project) {
    MavenPlugin plugin = MavenPlugin.getPlugin(project.getPom(), EmmaMavenPluginHandler.GROUP_ID, EmmaMavenPluginHandler.ARTIFACT_ID);
    if (plugin != null) {
      String path = plugin.getParameter("outputDirectory");
      if (path != null) {
        return new File(project.getFileSystem().resolvePath(path), "coverage.xml");
      }
    }
    return null;
  }

  private File getReportFromDefaultPath(Project project) {
    return new File(project.getFileSystem().getReportOutputDir(), "emma/coverage.xml");
  }

}
