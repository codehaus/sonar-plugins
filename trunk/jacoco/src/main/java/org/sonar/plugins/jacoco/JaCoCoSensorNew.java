/*
 * Copyright (c) 2010 Compuware Corp.
 * Sonar Plugin JaCoCo, open source software Sonar plugin.
 * mailto:anthony.dahanne@compuware.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice, this permission notice and the below disclaimer shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. WITHOUT LIMITING THE FOREGOING, COMPUWARE MAKES NO REPRESENTATIONS OR WARRANTIES CONCERNING THE COMPLETENESS, ACCURACY OR OPERATION OF THE SOFTWARE.  CLIENT SHALL HAVE THE SOLE RESPONSIBILITY FOR ADEQUATE PROTECTION AND BACKUP OF ITS DATA USED IN CONNECTION WITH THE SOFTWARE.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.sonar.plugins.jacoco;

import org.jacoco.core.analysis.ClassCoverage;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.ILines;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Analyzer;
import org.sonar.api.Plugins;
import org.sonar.api.batch.AbstractCoverageExtension;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Evgeny Mandrikov
 */
public class JaCoCoSensorNew extends AbstractCoverageExtension implements Sensor {

  public JaCoCoSensorNew(Plugins plugins) {
    super(plugins);
  }

  public void analyse(Project project, SensorContext context) {
    // TODO
  }

  protected void readExecutionData(File jacocoExecutionData, File buildOutputDir) throws IOException {
    // TODO

    SessionInfoStore sessionInfoStore = new SessionInfoStore();
    ExecutionDataStore executionDataStore = new ExecutionDataStore();

    ExecutionDataReader reader = new ExecutionDataReader(new FileInputStream(jacocoExecutionData));
    reader.setSessionInfoVisitor(sessionInfoStore);
    reader.setExecutionDataVisitor(executionDataStore);
    reader.read();

    CoverageBuilder coverageBuilder = new CoverageBuilder(executionDataStore);

    final Analyzer analyzer = new Analyzer(coverageBuilder);
    analyzer.analyzeAll(buildOutputDir);

    for (ClassCoverage cc : coverageBuilder.getClasses()) {
      System.out.println(cc.getSourceFileName());
      System.out.println(cc.getPackageName());
      System.out.println(cc.getName());
      
      final ILines lines = cc.getLines();
      for (int i = lines.getFirstLine(); i <= lines.getLastLine(); i++) {
        System.out.printf("Line %s: %s%n", i, getColor(lines.getStatus(i)));
      }
    }
  }

  private String getColor(final int status) {
    switch (status) {
      case ILines.NOT_COVERED:
        return "not covered";
      case ILines.PARTLY_COVERED:
        return "partly_covered";
      case ILines.FULLY_COVERED:
        return "fully_covered";
    }
    return "";
  }
}
