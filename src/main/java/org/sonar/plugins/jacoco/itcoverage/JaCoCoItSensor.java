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

package org.sonar.plugins.jacoco.itcoverage;

import org.apache.commons.lang.StringUtils;
import org.jacoco.core.analysis.ICounter;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.plugins.jacoco.AbstractAnalyzer;
import org.sonar.plugins.jacoco.JacocoConfiguration;

/**
 * Note that this class can't extend {@link org.sonar.api.batch.AbstractCoverageExtension}, because in this case this extension will be
 * disabled under Sonar
 * 2.3, if JaCoCo is not defined as the default code coverage plugin.
 * 
 * @author Evgeny Mandrikov
 */
public class JaCoCoItSensor implements Sensor {
  private JacocoConfiguration configuration;

  public JaCoCoItSensor(JacocoConfiguration configuration) {
    this.configuration = configuration;
  }

  public boolean shouldExecuteOnProject(Project project) {
    return StringUtils.isNotBlank(configuration.getItReportPath())
        && project.getAnalysisType().isDynamic(true);
  }

  public void analyse(Project project, SensorContext context) {
    new Analyzer().analyse(project, context);
  }

  public class Analyzer extends AbstractAnalyzer {
    @Override
    protected String getReportPath(Project project) {
      return configuration.getItReportPath();
    }

    @Override
    protected void saveMeasures(SensorContext context, JavaFile resource, ICounter lines, String lineHitsData,
        double totalBranches, double totalCoveredBranches, String branchHitsData) {
      context.saveMeasure(resource, JaCoCoItMetrics.IT_LINES_TO_COVER, (double) lines.getTotalCount());
      context.saveMeasure(resource, JaCoCoItMetrics.IT_UNCOVERED_LINES, (double) lines.getMissedCount());
      Measure lineHits = new Measure(JaCoCoItMetrics.IT_COVERAGE_LINE_HITS_DATA).setData(lineHitsData);
      context.saveMeasure(resource, lineHits.setPersistenceMode(PersistenceMode.DATABASE));

      if (totalBranches > 0) {
        context.saveMeasure(resource, JaCoCoItMetrics.IT_CONDITIONS_TO_COVER, totalBranches);
        context.saveMeasure(resource, JaCoCoItMetrics.IT_UNCOVERED_CONDITIONS, totalBranches - totalCoveredBranches);
        Measure branchHits = new Measure(JaCoCoItMetrics.IT_BRANCH_COVERAGE_HITS_DATA).setData(branchHitsData);
        context.saveMeasure(resource, branchHits.setPersistenceMode(PersistenceMode.DATABASE));
      }
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
