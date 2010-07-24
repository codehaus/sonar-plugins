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

package org.sonar.plugins.jacoco;

import org.apache.commons.lang.StringUtils;
import org.jacoco.core.analysis.*;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Analyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.Plugins;
import org.sonar.api.batch.AbstractCoverageExtension;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.maven.DependsUponMavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.PersistenceMode;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Evgeny Mandrikov
 */
public class JaCoCoSensor extends AbstractCoverageExtension implements Sensor, DependsUponMavenPlugin {

  private Logger logger = LoggerFactory.getLogger(getClass());
  private SurefireMavenPluginHandler handler;
  private PropertiesBuilder<Integer, Integer> lineHitsBuilder = new PropertiesBuilder<Integer, Integer>(CoreMetrics.COVERAGE_LINE_HITS_DATA);

  public JaCoCoSensor(Plugins plugins, SurefireMavenPluginHandler handler) {
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
    String path = getPath(project);
    File jacocoExecutionData = project.getFileSystem().resolvePath(path);
    if (checkReportAvailability(jacocoExecutionData)) {
      try {
        readExecutionData(jacocoExecutionData, project.getFileSystem().getBuildOutputDir(), context);
      } catch (IOException e) {
        throw new SonarException(e);
      }
    }
  }

  public static String getPath(Project project) {
    return project.getConfiguration().getString(JaCoCoPlugin.REPORT_PATH_PROPERTY, JaCoCoPlugin.REPORT_PATH_DEFAULT_VALUE);
  }

  private boolean checkReportAvailability(File report) {
    if (report == null || !report.exists() || !report.isFile()) {
      logger.warn("Can't find JaCoCo execution data : {}. Project coverage is set to 0%.", report);
      return false;
    }
    logger.info("Analysing {}", report.getAbsolutePath());
    return true;
  }

  protected void readExecutionData(File jacocoExecutionData, File buildOutputDir, SensorContext context) throws IOException {
    SessionInfoStore sessionInfoStore = new SessionInfoStore();
    ExecutionDataStore executionDataStore = new ExecutionDataStore();

    ExecutionDataReader reader = new ExecutionDataReader(new FileInputStream(jacocoExecutionData));
    reader.setSessionInfoVisitor(sessionInfoStore);
    reader.setExecutionDataVisitor(executionDataStore);
    reader.read();

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
          logger.warn("Unknown status for line {} in {}", lineId, resource);
          continue;
      }
      lineHitsBuilder.add(lineId, fakeHits);
    }

    context.saveMeasure(resource, CoreMetrics.LINES_TO_COVER, (double) lines.getTotalCount());
    context.saveMeasure(resource, CoreMetrics.UNCOVERED_LINES, (double) lines.getMissedCount());
    context.saveMeasure(resource, lineHitsBuilder.build().setPersistenceMode(PersistenceMode.DATABASE));
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
