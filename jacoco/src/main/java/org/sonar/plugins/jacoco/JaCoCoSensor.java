/*
 * Sonar JaCoCo Plugin
 * Copyright (C) 2010 SonarSource
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

package org.sonar.plugins.jacoco;

import org.jacoco.core.analysis.ILines;
import org.sonar.api.Plugins;
import org.sonar.api.batch.AbstractCoverageExtension;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.maven.DependsUponMavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;

/**
 * @author Evgeny Mandrikov
 */
public class JaCoCoSensor extends AbstractCoverageExtension implements Sensor, DependsUponMavenPlugin {
  private JaCoCoMavenPluginHandler handler;

  public JaCoCoSensor(Plugins plugins, JaCoCoMavenPluginHandler handler) {
    super(plugins);
    this.handler = handler;
  }

  public MavenPluginHandler getMavenPluginHandler(Project project) {
    if (project.getAnalysisType().equals(Project.AnalysisType.DYNAMIC)) {
      return handler;
    }
    return null;
  }

  public void analyse(Project project, SensorContext context) {
    new Analyzer().analyse(project, context);
  }

  public static class Analyzer extends AbstractAnalyzer {
    @Override
    protected String getReportPath(Project project) {
      return getPath(project);
    }

    @Override
    protected void saveMeasures(SensorContext context, JavaFile resource, ILines lines, String lineHitsData,
        double totalBranches, double totalCoveredBranches, String branchHitsData) {
      context.saveMeasure(resource, CoreMetrics.LINES_TO_COVER, (double) lines.getTotalCount());
      context.saveMeasure(resource, CoreMetrics.UNCOVERED_LINES, (double) lines.getMissedCount());
      Measure lineHits = new Measure(CoreMetrics.COVERAGE_LINE_HITS_DATA).setData(lineHitsData);
      context.saveMeasure(resource, lineHits.setPersistenceMode(PersistenceMode.DATABASE));

      if (totalBranches > 0) {
        context.saveMeasure(resource, CoreMetrics.CONDITIONS_TO_COVER, totalBranches);
        context.saveMeasure(resource, CoreMetrics.UNCOVERED_CONDITIONS, totalBranches - totalCoveredBranches);
        Measure branchHits = new Measure(CoreMetrics.BRANCH_COVERAGE_HITS_DATA).setData(branchHitsData);
        context.saveMeasure(resource, branchHits.setPersistenceMode(PersistenceMode.DATABASE));
      }
    }
  }

  public static String getPath(Project project) {
    return project.getConfiguration().getString(JaCoCoPlugin.REPORT_PATH_PROPERTY, JaCoCoPlugin.REPORT_PATH_DEFAULT_VALUE);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
