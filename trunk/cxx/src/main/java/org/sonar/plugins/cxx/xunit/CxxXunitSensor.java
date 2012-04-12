/*
 * Sonar Cxx Plugin, open source software quality management tool.
 * Copyright (C) 2010 - 2011, Neticoa SAS France - Tous droits reserves.
 * Author(s) : Franck Bonin, Neticoa SAS France.
 *
 * Sonar Cxx Plugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar Cxx Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar Cxx Plugin; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.cxx.xunit;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.AbstractCoverageExtension;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.ParsingUtils;
import org.sonar.api.utils.StaxParser;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.cxx.utils.CxxSensor;
import org.sonar.plugins.cxx.utils.CxxUtils;

/**
 * {@inheritDoc}
 */
public class CxxXunitSensor extends CxxSensor {
  //
  // This is basically a copy of sonar-surefire-plugin with xsl transformation
  // thrown in and a small change allowing the 'time'-attribute to be empty
  // This code (in this) should die: it should reuse the AbstractSurefireParser
  // as soon as it is suitable for this.
  // 
  
  public static final String REPORT_PATH_KEY = "sonar.cxx.xunit.reportPath";
  public static final String XSLT_URL_KEY = "sonar.cxx.xunit.xsltURL";
  private static final String DEFAULT_REPORT_PATH = "xunit-reports/xunit-result-*.xml";
  private String xsltURL = null;
  private Configuration conf = null;

  /**
   * {@inheritDoc}
   */
  public CxxXunitSensor(Configuration conf) {
    this.conf = conf;
    xsltURL = conf.getString(XSLT_URL_KEY);
  }
  
  /**
   * {@inheritDoc}
   */
  @DependsUpon
  public Class<?> dependsUponCoverageSensors() {
    return AbstractCoverageExtension.class;
  }

  /**
   * {@inheritDoc}
   */
  public void analyse(Project project, SensorContext context) {
    Exception exc = null;
    try {
      List<File> reports = getReports(conf, project.getFileSystem().getBasedir().getPath(),
                                      REPORT_PATH_KEY, DEFAULT_REPORT_PATH);
      if (reports.isEmpty()) {
        insertZeroWhenNoReports(project, context);
      } else {
        transformReport(project, reports, context);
        parseReport(project, reports, context);
      }
    } catch (java.io.IOException e) {
      exc = e;
    } catch (javax.xml.transform.TransformerException e) {
      exc = e;
    } catch (javax.xml.stream.XMLStreamException e) {
      exc = e;
    }
    
    if (exc != null) {
      String msg = new StringBuilder()
        .append("Cannot feed the data into sonar, details: '")
        .append(exc)
        .append("'")
        .toString();
      throw new SonarException(msg, exc);
    }
  }
  
  private void insertZeroWhenNoReports(Project pom, SensorContext context) {
    if (!StringUtils.equalsIgnoreCase("pom", pom.getPackaging())) {
      context.saveMeasure(CoreMetrics.TESTS, 0.0);
    }
  }
  
  void transformReport(Project project, List<File> reports, SensorContext context)
    throws java.io.IOException, javax.xml.transform.TransformerException
  {
    if (xsltURL != null) {
      CxxUtils.LOG.debug("Transforming the report using xslt '{}'", xsltURL);
      InputStream inputStream = this.getClass().getResourceAsStream("/xsl/" + xsltURL);
      if (inputStream == null) {
        URL url = new URL(xsltURL);
        inputStream = url.openStream();
      }
      
      Source xsl = new StreamSource(inputStream);
      TransformerFactory factory = TransformerFactory.newInstance();
      Templates template = factory.newTemplates(xsl);
      Transformer xformer = template.newTransformer();
      xformer.setOutputProperty(OutputKeys.INDENT, "yes");
      
      for (int i = 0; i < reports.size(); i++) {
        Source source = new StreamSource(reports.get(i));
        File fileOutPut = new File(reports.get(i).getAbsolutePath() + ".after_xslt");
        Result result = new StreamResult(fileOutPut);
        xformer.transform(source, result);
        reports.set(i, fileOutPut);
      }
    } else {
      CxxUtils.LOG.debug("Transformation skipped: no xslt given");
    }
  }
  
  private void parseReport(Project project, List<File> reports, SensorContext context)
    throws javax.xml.stream.XMLStreamException
  {
    Set<TestSuiteReport> analyzedReports = new HashSet<TestSuiteReport>();
    for (File report : reports) {
      CxxUtils.LOG.info("Parsing report '{}'", report);
      TestSuiteParser parserHandler = new TestSuiteParser();
      StaxParser parser = new StaxParser(parserHandler, false);
      parser.parse(report);
      
      for (TestSuiteReport fileReport : parserHandler.getParsedReports()) {
        if (fileReport.isValid() && !analyzedReports.contains(fileReport)
            && fileReport.getTests() > 0) {
          double testsCount = fileReport.getTests() - fileReport.getSkipped();
          saveClassMeasure(project, context, fileReport, CoreMetrics.SKIPPED_TESTS, fileReport.getSkipped());
          saveClassMeasure(project, context, fileReport, CoreMetrics.TESTS, testsCount);
          saveClassMeasure(project, context, fileReport, CoreMetrics.TEST_ERRORS, fileReport.getErrors());
          saveClassMeasure(project, context, fileReport, CoreMetrics.TEST_FAILURES, fileReport.getFailures());
          saveClassMeasure(project, context, fileReport, CoreMetrics.TEST_EXECUTION_TIME, fileReport.getTimeMS());
          double passedTests = testsCount - fileReport.getErrors() - fileReport.getFailures();
          if (testsCount > 0) {
            double percentage = passedTests * 100d / testsCount;
            saveClassMeasure(project, context, fileReport, CoreMetrics.TEST_SUCCESS_DENSITY, ParsingUtils.scaleValue(percentage));
          }
          saveTestsDetails(project, context, fileReport);
          analyzedReports.add(fileReport);
        }
      }
    }
  }

  private void saveTestsDetails(Project project, SensorContext context, TestSuiteReport fileReport) {
    StringBuilder testCaseDetails = new StringBuilder();
    testCaseDetails.append("<tests-details>");
    List<TestCaseDetails> details = fileReport.getDetails();
    for (TestCaseDetails detail : details) {
      testCaseDetails.append("<testcase status=\"").append(detail.getStatus()).append("\" time=\"").append(detail.getTimeMS())
        .append("\" name=\"").append(detail.getName()).append("\"");
      boolean isError = detail.getStatus().equals(TestCaseDetails.STATUS_ERROR);
      if (isError || detail.getStatus().equals(TestCaseDetails.STATUS_FAILURE)) {
        testCaseDetails.append(">").append(isError ? "<error message=\"" : "<failure message=\"")
          .append(StringEscapeUtils.escapeXml(detail.getErrorMessage())).append("\">").append("<![CDATA[")
          .append(StringEscapeUtils.escapeXml(detail.getStackTrace())).append("]]>").append(isError ? "</error>" : "</failure>")
          .append("</testcase>");
      } else {
        testCaseDetails.append("/>");
      }
    }
    testCaseDetails.append("</tests-details>");

    CxxUtils.LOG.debug("Saving test execution measures for file '{}'", fileReport.getClassKey());
    context.saveMeasure(getUnitTestResource(project, fileReport), new Measure(CoreMetrics.TEST_DATA, testCaseDetails.toString()));
  }

  private void saveClassMeasure(Project project, SensorContext context, TestSuiteReport fileReport, Metric metric, double value) {
    if ( !Double.isNaN(value)) {
      context.saveMeasure(getUnitTestResource(project, fileReport), metric, value);
    }
  }

  private Resource<?> getUnitTestResource(Project project, TestSuiteReport fileReport) {
    return org.sonar.api.resources.File.fromIOFile(new File(fileReport.getClassKey()),
                                                   project);
  }
}
