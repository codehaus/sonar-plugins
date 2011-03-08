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

import org.apache.commons.lang.StringUtils;
import org.jacoco.core.analysis.*;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.sonar.api.batch.SensorContext;
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
public abstract class AbstractAnalyzer {

  public void analyse(Project project, SensorContext context) {
    final File buildOutputDir = project.getFileSystem().getBuildOutputDir();
    if (!buildOutputDir.exists()) {
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

    CoverageBuilder coverageBuilder = new CoverageBuilder();
    Analyzer analyzer = new Analyzer(executionDataStore, coverageBuilder);
    analyzeAll(analyzer, buildOutputDir);

    for (ISourceFileCoverage coverage : coverageBuilder.getSourceFiles()) {
      String fileName = StringUtils.substringBeforeLast(coverage.getName(), ".");
      String resourceName = StringUtils.replaceChars(coverage.getPackageName() + "/" + fileName, '/', '.');

      JavaFile resource = new JavaFile(resourceName);
      analyzeClass(resource, coverage, context);
    }
  }

  /**
   * Copied from {@link Analyzer#analyzeAll(File)} in order to add logging.
   */
  private void analyzeAll(Analyzer analyzer, File file) {
    if (file.isDirectory()) {
      for (File f : file.listFiles()) {
        analyzeAll(analyzer, f);
      }
    } else {
      try {
        analyzer.analyzeAll(file);
      } catch (Exception e) {
        JaCoCoUtils.LOG.warn("Exception during analysis of file " + file.getAbsolutePath(), e);
      }
    }
  }

  private void analyzeClass(JavaFile resource, ISourceFileCoverage coverage, SensorContext context) {
    if (context.getResource(resource) == null) {
      // Do not save measures on resource which doesn't exist in the context
      return;
    }

    PropertiesBuilder<Integer, Integer> lineHitsBuilder = new PropertiesBuilder<Integer, Integer>();
    PropertiesBuilder<Integer, String> branchHitsBuilder = new PropertiesBuilder<Integer, String>();
    double totalBranches = 0;
    double totalCoveredBranches = 0;

    for (int lineId = coverage.getFirstLine(); lineId <= coverage.getLastLine(); lineId++) {
      final int fakeHits;
      ILine line = coverage.getLine(lineId);
      switch (line.getInstructionCounter().getStatus()) {
        case ICounter.FULLY_COVERED:
          fakeHits = 1;
          break;
        case ICounter.PARTLY_COVERED:
          fakeHits = 1;
          break;
        case ICounter.NOT_COVERED:
          fakeHits = 0;
          break;
        case ICounter.EMPTY:
          continue;
        default:
          JaCoCoUtils.LOG.warn("Unknown status for line {} in {}", lineId, resource);
          continue;
      }
      lineHitsBuilder.add(lineId, fakeHits);

      ICounter branchCounter = line.getBranchCounter();
      double lineBranches = branchCounter.getTotalCount();

      if (lineBranches > 0) {
        double lineCoveredBranches = branchCounter.getCoveredCount();
        double lineBranchCoverage = 100 * lineCoveredBranches / lineBranches;
        totalBranches += lineBranches;
        totalCoveredBranches += lineCoveredBranches;
        branchHitsBuilder.add(lineId, Math.round(lineBranchCoverage) + "%");
      }
    }

    saveMeasures(context, resource, coverage.getLineCounter(), lineHitsBuilder.buildData(), totalBranches, totalCoveredBranches,
        branchHitsBuilder.buildData());
  }

  protected abstract void saveMeasures(SensorContext context, JavaFile resource, ICounter lines, String lineHitsData,
      double totalBranches, double totalCoveredBranches, String branchHitsData);

  protected abstract String getReportPath(Project project);

}
