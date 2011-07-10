/*
 * Sonar Emma plugin
 * Copyright (C) 2009 SonarSource
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
package org.sonar.plugins.emma;

import org.apache.commons.configuration.Configuration;
import org.sonar.api.batch.CoverageExtension;
import org.sonar.api.batch.Initializer;
import org.sonar.api.batch.maven.DependsUponMavenPlugin;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.resources.Project;

/**
 * Provides {@link EmmaMavenPluginHandler} and configures correct path to report.
 * Enabled only in Maven environment.
 */
public class EmmaMavenInitializer extends Initializer implements CoverageExtension, DependsUponMavenPlugin {

  private EmmaMavenPluginHandler handler;

  public EmmaMavenInitializer(EmmaMavenPluginHandler handler) {
    this.handler = handler;
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return project.getAnalysisType().isDynamic(true) &&
        project.getFileSystem().hasJavaSourceFiles();
  }

  public MavenPluginHandler getMavenPluginHandler(Project project) {
    if (project.getAnalysisType().equals(Project.AnalysisType.DYNAMIC)) {
      return handler;
    }
    return null;
  }

  @Override
  public void execute(Project project) {
    Configuration conf = project.getConfiguration();
    if (!conf.containsKey(EmmaPlugin.REPORT_PATH_PROPERTY)) {
      String report = getReportFromPluginConfiguration(project);
      if (report == null) {
        report = getReportFromDefaultPath(project);
      }
      conf.setProperty(EmmaPlugin.REPORT_PATH_PROPERTY, report);
    }
  }

  private String getReportFromPluginConfiguration(Project project) {
    MavenPlugin mavenPlugin = MavenPlugin.getPlugin(project.getPom(), EmmaMavenPluginHandler.GROUP_ID, EmmaMavenPluginHandler.ARTIFACT_ID);
    if (mavenPlugin != null) {
      return mavenPlugin.getParameter("outputDirectory");
    }
    return null;
  }

  private String getReportFromDefaultPath(Project project) {
    return project.getFileSystem().getBuildDir().toString();
  }

}
