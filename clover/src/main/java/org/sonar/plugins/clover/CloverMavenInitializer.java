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

import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.CoverageExtension;
import org.sonar.api.batch.Initializer;
import org.sonar.api.batch.maven.DependsUponMavenPlugin;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;

/**
 * Provides {@link CloverMavenPluginHandler} and configures correct path to report.
 * Enabled only in Maven environment.
 */
public class CloverMavenInitializer extends Initializer implements CoverageExtension, DependsUponMavenPlugin {

  private CloverMavenPluginHandler handler;
  private CloverSettings settings;

  public CloverMavenInitializer(CloverMavenPluginHandler handler, CloverSettings settings) {
    this.handler = handler;
    this.settings = settings;
  }

  public MavenPluginHandler getMavenPluginHandler(Project project) {
    if (project.getAnalysisType().equals(Project.AnalysisType.DYNAMIC)) {
      return handler;
    }
    return null;
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return settings.isEnabled(project);
  }

  @Override
  public void execute(Project project) {
    if (StringUtils.isEmpty(settings.getReportPath())) {
      String report = getReportPathFromMavenPlugin(project);
      if (report == null) {
        report = getDefaultReportPath(project);
      }
      settings.setReportPath(report);
    }
  }

  private String getDefaultReportPath(Project project) {
    return project.getFileSystem().getReportOutputDir() + "/clover/clover.xml";
  }

  private String getReportPathFromMavenPlugin(Project project) {
    MavenPlugin plugin = MavenPlugin.getPlugin(project.getPom(), CloverConstants.MAVEN_GROUP_ID, CloverConstants.MAVEN_ARTIFACT_ID);
    if (plugin != null) {
      String path = plugin.getParameter("outputDirectory");
      if (path != null) {
        return path + "/clover.xml";
      }
    }
    return null;
  }
}
