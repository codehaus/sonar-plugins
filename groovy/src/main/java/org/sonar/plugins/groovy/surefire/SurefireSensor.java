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

package org.sonar.plugins.groovy.surefire;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.AbstractCoverageExtension;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenSurefireUtils;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.ParsingUtils;
import org.sonar.api.utils.StaxParser;
import org.sonar.api.utils.XmlParserException;
import org.sonar.plugins.groovy.foundation.Groovy;
import org.sonar.plugins.groovy.foundation.GroovyFile;

/**
 * TODO copied from sonar-surefire-plugin with modifications: JavaFile replaced by GroovyFile
 */
public class SurefireSensor implements Sensor {

  private static Logger logger = LoggerFactory.getLogger(SurefireSensor.class);

  @DependsUpon
  public Class<?> dependsUponCoverageSensors() {
    return AbstractCoverageExtension.class;
  }

  public boolean shouldExecuteOnProject(Project project) {
    return project.getAnalysisType().isDynamic(true) && Groovy.KEY.equals(project.getLanguageKey());
  }

  public void analyse(Project project, SensorContext context) {
    File dir = getReportsDirectory(project);
    collect(project, context, dir);
  }

  protected File getReportsDirectory(Project project) {
    File dir = getReportsDirectoryFromProperty(project);
    if (dir == null) {
      dir = getReportsDirectoryFromPluginConfiguration(project);
    }
    if (dir == null) {
      dir = getReportsDirectoryFromDefaultConfiguration(project);
    }
    return dir;
  }

  private File getReportsDirectoryFromProperty(Project project) {
    String path = (String) project.getProperty(CoreProperties.SUREFIRE_REPORTS_PATH_PROPERTY);
    if (path != null) {
      return project.getFileSystem().resolvePath(path);
    }
    return null;
  }

  private File getReportsDirectoryFromPluginConfiguration(Project project) {
    MavenPlugin plugin = MavenPlugin.getPlugin(project.getPom(), MavenSurefireUtils.GROUP_ID, MavenSurefireUtils.ARTIFACT_ID);
    if (plugin != null) {
      String path = plugin.getParameter("reportsDirectory");
      if (path != null) {
        return project.getFileSystem().resolvePath(path);
      }
    }
    return null;
  }

  private File getReportsDirectoryFromDefaultConfiguration(Project project) {
    return new File(project.getFileSystem().getBuildDir(), "surefire-reports");
  }

  private File[] getReports(File dir) {
    if (dir == null || !dir.isDirectory() || !dir.exists()) {
      return new File[0];
    }
    return dir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.startsWith("TEST") && name.endsWith(".xml");
      }
    });
  }

  protected void collect(Project project, SensorContext context, File reportsDir) {
    logger.info("parsing {}", reportsDir);
    File[] xmlFiles = getReports(reportsDir);

    if (xmlFiles.length == 0) {
      insertZeroWhenNoReports(project, context);
    } else {
      parseFiles(context, xmlFiles);
    }
  }

  private void insertZeroWhenNoReports(Project pom, SensorContext context) {
    if ( !StringUtils.equalsIgnoreCase("pom", pom.getPackaging())) {
      context.saveMeasure(CoreMetrics.TESTS, 0.0);
    }
  }

  private void parseFiles(SensorContext context, File[] reports) {
    Set<TestSuiteReport> analyzedReports = new HashSet<TestSuiteReport>();
    try {
      for (File report : reports) {
        TestSuiteParser parserHandler = new TestSuiteParser();
        StaxParser parser = new StaxParser(parserHandler, false);
        parser.parse(report);

        for (TestSuiteReport fileReport : parserHandler.getParsedReports()) {
          if ( !fileReport.isValid() || analyzedReports.contains(fileReport)) {
            continue;
          }
          if (fileReport.getTests() > 0) {
            double testsCount = fileReport.getTests() - fileReport.getSkipped();
            saveClassMeasure(context, fileReport, CoreMetrics.SKIPPED_TESTS, fileReport.getSkipped());
            saveClassMeasure(context, fileReport, CoreMetrics.TESTS, testsCount);
            saveClassMeasure(context, fileReport, CoreMetrics.TEST_ERRORS, fileReport.getErrors());
            saveClassMeasure(context, fileReport, CoreMetrics.TEST_FAILURES, fileReport.getFailures());
            saveClassMeasure(context, fileReport, CoreMetrics.TEST_EXECUTION_TIME, fileReport.getTimeMS());
            double passedTests = testsCount - fileReport.getErrors() - fileReport.getFailures();
            if (testsCount > 0) {
              double percentage = passedTests * 100d / testsCount;
              saveClassMeasure(context, fileReport, CoreMetrics.TEST_SUCCESS_DENSITY, ParsingUtils.scaleValue(percentage));
            }
            saveTestsDetails(context, fileReport);
            analyzedReports.add(fileReport);
          }
        }
      }

    } catch (Exception e) {
      throw new XmlParserException("Can not parse surefire reports", e);
    }
  }

  private void saveTestsDetails(SensorContext context, TestSuiteReport fileReport) throws TransformerException {
    StringBuilder testCaseDetails = new StringBuilder(256);
    testCaseDetails.append("<tests-details>");
    List<TestCaseDetails> details = fileReport.getDetails();
    for (TestCaseDetails detail : details) {
      testCaseDetails.append("<testcase status=\"").append(detail.getStatus())
          .append("\" time=\"").append(detail.getTimeMS())
          .append("\" name=\"").append(detail.getName()).append("\"");
      boolean isError = detail.getStatus().equals(TestCaseDetails.STATUS_ERROR);
      if (isError || detail.getStatus().equals(TestCaseDetails.STATUS_FAILURE)) {
        testCaseDetails.append(">")
            .append(isError ? "<error message=\"" : "<failure message=\"")
            .append(StringEscapeUtils.escapeXml(detail.getErrorMessage())).append("\">")
            .append("<![CDATA[").append(StringEscapeUtils.escapeXml(detail.getStackTrace())).append("]]>")
            .append(isError ? "</error>" : "</failure>").append("</testcase>");
      } else {
        testCaseDetails.append("/>");
      }
    }
    testCaseDetails.append("</tests-details>");
    context.saveMeasure(getUnitTestResource(fileReport), new Measure(CoreMetrics.TEST_DATA, testCaseDetails.toString()));
  }

  private void saveClassMeasure(SensorContext context, TestSuiteReport fileReport, Metric metric, double value) {
    if ( !Double.isNaN(value)) {
      context.saveMeasure(getUnitTestResource(fileReport), metric, value);
    }
  }

  private Resource<?> getUnitTestResource(TestSuiteReport fileReport) {
    return new GroovyFile(fileReport.getClassKey(), true);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "ForGroovy";
  }
}
