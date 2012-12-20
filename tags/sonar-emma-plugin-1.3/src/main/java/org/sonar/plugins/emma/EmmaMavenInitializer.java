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

import org.apache.commons.lang.StringUtils;
import org.apache.maven.project.MavenProject;
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
  private EmmaSettings settings;
  private MavenProject mavenProject;

  public EmmaMavenInitializer(EmmaMavenPluginHandler handler, EmmaSettings settings, MavenProject mavenProject) {
    this.handler = handler;
    this.settings = settings;
    this.mavenProject = mavenProject;
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return settings.isEnabled(project);
  }

  public MavenPluginHandler getMavenPluginHandler(Project project) {
    return project.getAnalysisType().equals(Project.AnalysisType.DYNAMIC) ? handler : null;
  }

  @Override
  public void execute(Project project) {
    if (StringUtils.isBlank(settings.getReportPath())) {
      String report = getReportFromMavenPlugin();
      if (report == null) {
        report = getReportFromDefaultPath(project);
      }
      settings.setReportPath(report);
    }
  }

  private String getReportFromMavenPlugin() {
    MavenPlugin mavenPlugin = MavenPlugin.getPlugin(mavenProject, EmmaMavenPluginHandler.GROUP_ID, EmmaMavenPluginHandler.ARTIFACT_ID);
    if (mavenPlugin != null) {
      return mavenPlugin.getParameter("outputDirectory");
    }
    return null;
  }

  private String getReportFromDefaultPath(Project project) {
    return project.getFileSystem().getBuildDir().toString();
  }

}
