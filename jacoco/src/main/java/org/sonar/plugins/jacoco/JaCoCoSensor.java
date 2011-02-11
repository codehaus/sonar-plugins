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

import org.jacoco.core.analysis.ICounter;
import org.sonar.api.batch.AbstractCoverageExtension;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;

/**
 * @author Evgeny Mandrikov
 */
public class JaCoCoSensor extends AbstractCoverageExtension implements Sensor {

  private JacocoConfiguration configuration;

  public JaCoCoSensor(JacocoConfiguration configuration) {
    this.configuration = configuration;
  }

  public void analyse(Project project, SensorContext context) {
    new Analyzer().analyse(project, context);
  }

  public class Analyzer extends AbstractAnalyzer {
    @Override
    protected String getReportPath(Project project) {
      return configuration.getReportPath();
    }

    @Override
    protected void saveMeasures(SensorContext context, JavaFile resource, ICounter lines, String lineHitsData,
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

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
