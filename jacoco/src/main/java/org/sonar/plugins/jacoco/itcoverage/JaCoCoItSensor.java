/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SonarSource
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

package org.sonar.plugins.jacoco.itcoverage;

import org.apache.commons.lang.StringUtils;
import org.jacoco.core.analysis.ILines;
import org.sonar.api.batch.AbstractCoverageExtension;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.plugins.jacoco.AbstractAnalyzer;
import org.sonar.plugins.jacoco.JaCoCoPlugin;

/**
 * Note that this class can't extend {@link AbstractCoverageExtension}, because in this case this extension will be disabled under Sonar
 * 2.3, if JaCoCo is not defined as the default code coverage plugin.
 * 
 * @author Evgeny Mandrikov
 */
public class JaCoCoItSensor implements Sensor {
  public JaCoCoItSensor() {
  }

  public boolean shouldExecuteOnProject(Project project) {
    return StringUtils.isNotBlank(project.getConfiguration().getString(JaCoCoPlugin.IT_REPORT_PATH_PROPERTY))
        && project.getAnalysisType().isDynamic(true);
  }

  public void analyse(Project project, SensorContext context) {
    new Analyzer().analyse(project, context);
  }

  public static class Analyzer extends AbstractAnalyzer {
    @Override
    protected String getReportPath(Project project) {
      return project.getConfiguration().getString(JaCoCoPlugin.IT_REPORT_PATH_PROPERTY, JaCoCoPlugin.IT_REPORT_PATH_DEFAULT_VALUE);
    }

    @Override
    protected void saveMeasures(SensorContext context, JavaFile resource, ILines lines, String lineHitsData) {
      context.saveMeasure(resource, JaCoCoItMetrics.IT_LINES_TO_COVER, (double) lines.getTotalCount());
      context.saveMeasure(resource, JaCoCoItMetrics.IT_UNCOVERED_LINES, (double) lines.getMissedCount());
      Measure lineHits = new Measure(JaCoCoItMetrics.IT_COVERAGE_LINE_HITS_DATA).setData(lineHitsData);
      context.saveMeasure(resource, lineHits.setPersistenceMode(PersistenceMode.DATABASE));
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
