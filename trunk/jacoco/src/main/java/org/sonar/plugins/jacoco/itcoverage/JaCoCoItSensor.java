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
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ILines;
import org.jacoco.core.analysis.SourceFileCoverage;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Analyzer;
import org.sonar.api.batch.AbstractCoverageExtension;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.jacoco.JaCoCoPlugin;
import org.sonar.plugins.jacoco.JaCoCoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Note that this class can't extend {@link AbstractCoverageExtension}, because in this case this extension will be disabled under Sonar
 * 2.3, if JaCoCo is not defined as the default code coverage plugin.
 * 
 * @author Evgeny Mandrikov
 */
public class JaCoCoItSensor implements Sensor {
  private PropertiesBuilder<Integer, Integer> lineHitsBuilder = new PropertiesBuilder<Integer, Integer>();

  public JaCoCoItSensor() {
  }

  public boolean shouldExecuteOnProject(Project project) {
    return project.getAnalysisType().isDynamic(true);
  }

  public void analyse(Project project, SensorContext context) {
    final File buildOutputDir = project.getFileSystem().getBuildOutputDir();
    if ( !buildOutputDir.exists()) {
      JaCoCoUtils.LOG.info("Can't find build output directory: {}. Skipping JaCoCo analysis.", buildOutputDir);
      return;
    }
    String path = getReportPath(project);
    File jacocoExecutionData = project.getFileSystem().resolvePath(path);
    try {
      readExecutionData(jacocoExecutionData, buildOutputDir, context);
    } catch (IOException e) {
      throw new SonarException(e);
    }
  }

  public void readExecutionData(File jacocoExecutionData, File buildOutputDir, SensorContext context) throws IOException {
    SessionInfoStore sessionInfoStore = new SessionInfoStore();
    ExecutionDataStore executionDataStore = new ExecutionDataStore();

    if (jacocoExecutionData == null || !jacocoExecutionData.exists() || !jacocoExecutionData.isFile()) {
      JaCoCoUtils.LOG.info("Can't find JaCoCo execution data : {}. Project coverage is set to 0%.", jacocoExecutionData);
    } else {
      JaCoCoUtils.LOG.info("Analysing {}", jacocoExecutionData);
      ExecutionDataReader reader = new ExecutionDataReader(new FileInputStream(jacocoExecutionData));
      reader.setSessionInfoVisitor(sessionInfoStore);
      reader.setExecutionDataVisitor(executionDataStore);
      reader.read();
    }

    CoverageBuilder coverageBuilder = new CoverageBuilder(executionDataStore);

    Analyzer analyzer = new Analyzer(coverageBuilder);
    analyzer.analyzeAll(buildOutputDir);

    for (SourceFileCoverage coverage : coverageBuilder.getSourceFiles()) {
      String fileName = StringUtils.substringBeforeLast(coverage.getName(), ".");
      String resourceName = StringUtils.replaceChars(coverage.getPackageName() + "/" + fileName, '/', '.');

      JavaFile resource = new JavaFile(resourceName);
      analyzeClass(resource, coverage, context);
    }
  }

  private void analyzeClass(JavaFile resource, ICoverageNode coverage, SensorContext context) {
    lineHitsBuilder.clear();

    final ILines lines = coverage.getLines();
    for (int lineId = lines.getFirstLine(); lineId <= lines.getLastLine(); lineId++) {
      final int fakeHits;
      switch (lines.getStatus(lineId)) {
        case ILines.FULLY_COVERED:
          fakeHits = 1;
          break;
        case ILines.PARTLY_COVERED:
        case ILines.NOT_COVERED:
          fakeHits = 0;
          break;
        case ILines.NO_CODE:
          continue;
        default:
          JaCoCoUtils.LOG.warn("Unknown status for line {} in {}", lineId, resource);
          continue;
      }
      lineHitsBuilder.add(lineId, fakeHits);
    }

    saveMeasures(context, resource, lines, lineHitsBuilder.buildData());
  }

  protected String getReportPath(Project project) {
    return project.getConfiguration().getString(JaCoCoPlugin.IT_REPORT_PATH_PROPERTY, JaCoCoPlugin.IT_REPORT_PATH_DEFAULT_VALUE);
  }

  protected void saveMeasures(SensorContext context, JavaFile resource, ILines lines, String lineHitsData) {
    if (context.getResource(resource) != null) {
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
