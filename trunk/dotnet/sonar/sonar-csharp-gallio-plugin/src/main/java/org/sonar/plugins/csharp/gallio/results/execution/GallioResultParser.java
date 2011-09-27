/*
 * Sonar C# Plugin :: Gallio
 * Copyright (C) 2010 Jose Chillan, Alexandre Victoor and SonarSource
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
package org.sonar.plugins.csharp.gallio.results.execution;

import static org.sonar.plugins.csharp.gallio.helper.StaxHelper.advanceCursor;
import static org.sonar.plugins.csharp.gallio.helper.StaxHelper.descendantElements;
import static org.sonar.plugins.csharp.gallio.helper.StaxHelper.descendantSpecifiedElements;
import static org.sonar.plugins.csharp.gallio.helper.StaxHelper.findAttributeValue;
import static org.sonar.plugins.csharp.gallio.helper.StaxHelper.findElementName;
import static org.sonar.plugins.csharp.gallio.helper.StaxHelper.isAStartElement;
import static org.sonar.plugins.csharp.gallio.helper.StaxHelper.nextPosition;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMEvent;
import org.codehaus.staxmate.in.SMFilterFactory;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.csharp.gallio.results.execution.model.TestCaseDetail;
import org.sonar.plugins.csharp.gallio.results.execution.model.TestDescription;
import org.sonar.plugins.csharp.gallio.results.execution.model.TestStatus;
import org.sonar.plugins.csharp.gallio.results.execution.model.UnitTestReport;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Gallio result report parser.
 * 
 * @author Maxime SCHNEIDER-DUFEUTRELLE
 * 
 */
public class GallioResultParser implements BatchExtension {

  private static final String LOG_PATTERN = "--{} : {}";
  private static final String GALLIO_REPORT_PARSING_ERROR = "gallio report parsing error";
  private static final String GALLIO_URI = "http://www.gallio.org/";
  private static final String ASSEMBLY = "assembly";
  private static final String NAMESPACE = "namespace";
  private static final String TYPE = "type";
  private static final String MEMBER = "member";
  private static final String PATH = "path";
  private static final String LINE = "line";

  private static final Logger LOG = LoggerFactory.getLogger(GallioResultParser.class);

  private Map<String, TestCaseDetail> testCaseDetailsByTestIds;

  public Set<UnitTestReport> parse(File report) {
    try {
      testCaseDetailsByTestIds = new HashMap<String, TestCaseDetail>();
      SMInputFactory inf = new SMInputFactory(XMLInputFactory.newInstance());
      SMHierarchicCursor rootCursor = inf.rootElementCursor(report);
      advanceCursor(rootCursor);
      LOG.debug("rootCursor is at : {}", findElementName(rootCursor));

      // We first get the tests ids and put them in a map to get the details later
      Map<String, TestDescription> testsDetails = new HashMap<String, TestDescription>();

      QName testModelTag = new QName(GALLIO_URI, "testModel");
      SMInputCursor testModelCursor = descendantElements(rootCursor);
      testModelCursor.setFilter(SMFilterFactory.getElementOnlyFilter(testModelTag));
      advanceCursor(testModelCursor);
      LOG.debug("TestModelCursor initialized at : {}", findElementName(testModelCursor));
      testsDetails = recursiveParseTestsIds(testModelCursor, testsDetails, null, null);

      QName testPackageRunTag = new QName(GALLIO_URI, "testPackageRun");
      testModelCursor.setFilter(SMFilterFactory.getElementOnlyFilter(testPackageRunTag));
      advanceCursor(testModelCursor);
      String testId = "";
      recursiveParseTestsResults(testModelCursor, testId);

      // Finally, we fill the reports
      final Set<UnitTestReport> reports = createUnitTestsReport(testsDetails);
      rootCursor.getStreamReader().closeCompletely();
      LOG.debug("Parsing ended");

      return reports;
    } catch (XMLStreamException e) {
      throw new SonarException(GALLIO_REPORT_PARSING_ERROR, e);
    }

  }

  private Map<String, TestDescription> recursiveParseTestsIds(SMInputCursor rootCursor, Map<String, TestDescription> testDetails,
      File source, String parentAssemblyName) {
    File sourceFile = source;
    QName testTag = new QName(GALLIO_URI, "test");
    if (isAStartElement(rootCursor)) {
      // Get all the tests
      SMInputCursor currentTest = descendantSpecifiedElements(rootCursor, testTag);
      while (null != nextPosition(currentTest) && isAStartElement(currentTest)) {
        TestDescription testDescription = new TestDescription();
        String id = findAttributeValue(currentTest, "id");
        String name = findAttributeValue(currentTest, "name");
        String testCase = findAttributeValue(currentTest, "isTestCase");
        LOG.debug("Id : {} & isTestCase : {}", id, testCase);
        boolean isTestCase = "true".equals(testCase);
        // We analyse all the tests tags to get usefull informations if the test is a TestCase,
        // and to get their children
        SMInputCursor currentTestChildren = descendantElements(currentTest);
        String eltName = null;
        while (null != nextPosition(currentTestChildren) && !"parameters".equals(eltName)) {
          eltName = findElementName(currentTestChildren);
          if (isTestCase) {
            testDescription.setMethodName(name);
            LOG.debug(eltName);
            if ("codeReference".equals(eltName)) {
              parentAssemblyName = codeReferenceTreatment(parentAssemblyName, testDescription, currentTestChildren);
              retrieveCodeReferences(testDescription, currentTestChildren);
            }
            nextPosition(currentTestChildren);
            if ("codeLocation".equals(eltName)) {
              sourceFile = retrieveCodeLocation(sourceFile, testDescription, currentTestChildren);
            }
            if (null == testDescription.getSourceFile()) {
              testDescription.setSourceFile(sourceFile);
            }
            testDetails.put(id, testDescription);
          }
          sourceFile = evaluatePath(sourceFile, eltName, currentTestChildren);
          if ("children".equals(eltName)) {
            recursiveParseTestsIds(currentTestChildren, testDetails, sourceFile, parentAssemblyName);
          }
          advanceCursor(currentTestChildren);
        }
      }
    }
    return testDetails;
  }

  private String codeReferenceTreatment(String parentAssemblyName, TestDescription testDescription, SMInputCursor currentTestChildren) {
    String assemblyName = parentAssemblyName;
    String attributeValue;
    if (null != findAttributeValue(currentTestChildren, ASSEMBLY)) {
      attributeValue = findAttributeValue(currentTestChildren, ASSEMBLY);
      LOG.debug(LOG_PATTERN, ASSEMBLY, attributeValue);
      testDescription.setAssemblyName(StringUtils.substringBefore(attributeValue, ","));
      assemblyName = testDescription.getAssemblyName();
    } else {
      // Get the precedent assemblyName if not filled
      testDescription.setAssemblyName(assemblyName);
    }
    return assemblyName;
  }

  private File evaluatePath(File source, String eltName, SMInputCursor currentTestChildren) {
    File sourceFile = source;
    if ("codeLocation".equals(eltName) && null != findAttributeValue(currentTestChildren, PATH)) {
      File currentSourceFile = new File(findAttributeValue(currentTestChildren, PATH));
      if (currentSourceFile != null) {
        sourceFile = currentSourceFile;
      }
    }
    return sourceFile;
  }

  private void retrieveCodeReferences(TestDescription testDescription, SMInputCursor currentTestChildren) {
    String attributeValue;
    if (null != findAttributeValue(currentTestChildren, NAMESPACE)) {
      attributeValue = findAttributeValue(currentTestChildren, NAMESPACE);
      LOG.debug(LOG_PATTERN, NAMESPACE, attributeValue);
      testDescription.setNamespace(attributeValue);
    }
    if (null != findAttributeValue(currentTestChildren, TYPE)) {
      attributeValue = findAttributeValue(currentTestChildren, TYPE);
      LOG.debug(LOG_PATTERN, TYPE, attributeValue);
      testDescription.setClassName(attributeValue);
    }
    if (null != findAttributeValue(currentTestChildren, MEMBER)) {
      attributeValue = findAttributeValue(currentTestChildren, MEMBER);
      LOG.debug(LOG_PATTERN, MEMBER, attributeValue);
      testDescription.setMethodName(attributeValue);
    }
  }

  private File retrieveCodeLocation(File source, TestDescription testDescription, SMInputCursor currentTestChildren) {
    File sourceFile = source;
    String attributeValue;
    if (null != findAttributeValue(currentTestChildren, PATH)) {
      attributeValue = findAttributeValue(currentTestChildren, PATH);
      LOG.debug(LOG_PATTERN, PATH, attributeValue);
      File currentSourceFile = new File(attributeValue);
      testDescription.setSourceFile(currentSourceFile);
      sourceFile = currentSourceFile;
    }
    if (null != findAttributeValue(currentTestChildren, LINE)) {
      attributeValue = findAttributeValue(currentTestChildren, LINE);
      LOG.debug(LOG_PATTERN, LINE, attributeValue);
      int lineNumber = Integer.valueOf(attributeValue);
      testDescription.setLine(lineNumber);
    }
    return sourceFile;
  }

  private void recursiveParseTestsResults(SMInputCursor rootCursor, String testId) {
    String currentTestId = testId;
    QName testStepRunTag = new QName(GALLIO_URI, "testStepRun");
    SMInputCursor currentTestStepRun = descendantSpecifiedElements(rootCursor, testStepRunTag);
    String eltName = "";
    while (null != nextPosition(currentTestStepRun) && isAStartElement(currentTestStepRun)) {
      // currentTestTags represents the different tests
      SMInputCursor currentTestTags = descendantElements(currentTestStepRun);
      nextPosition(currentTestTags);
      eltName = findElementName(currentTestTags);
      if ("testStep".equals(eltName)) {
        if ("true".equals(findAttributeValue(currentTestTags, "isTestCase"))) {
          if (null != findAttributeValue(currentTestTags, "testId")) {
            currentTestId = findAttributeValue(currentTestTags, "testId");
            LOG.debug("--testId : {}", currentTestId);
            LOG.debug("--isTestCase : {}", findAttributeValue(currentTestTags, "isTestCase"));
            nextPosition(currentTestTags);
          }
          while (null != nextPosition(currentTestTags)) {
            TestCaseDetail testCaseDetail = parsingTags(currentTestTags, currentTestId);
            if (null != testCaseDetail) {
              testCaseDetailsByTestIds.put(currentTestId, testCaseDetail);
            }
          }
        } else {
          currentTestId = findAttributeValue(currentTestTags, "testId");
          while (null != nextPosition(currentTestTags)) {
            parseChildren(currentTestId, currentTestTags);
          }
        }
      }
      advanceCursor(currentTestTags);
    }
  }

  private void parseChildren(String testId, SMInputCursor currentTestTags) {
    if ("children".equals(findElementName(currentTestTags))) {
      recursiveParseTestsResults(currentTestTags, testId);
      nextPosition(currentTestTags);
    }
  }

  private TestCaseDetail parsingTags(SMInputCursor currentTestTags, String testId) {

    parseChildren(testId, currentTestTags);
    TestCaseDetail detail = new TestCaseDetail();
    if ("result".equals(findElementName(currentTestTags))) {
      LOG.debug("Result for test : {}", testId);

      String assertCount = findAttributeValue(currentTestTags, "assertCount");
      LOG.debug("---assertCount : {}", assertCount);
      detail.setCountAsserts((int) Double.parseDouble(assertCount));

      String duration = findAttributeValue(currentTestTags, "duration");
      LOG.debug("---duration : {}", duration);
      detail.setTimeMillis((int) Math.round(Double.parseDouble(duration) * 1000.));

      SMInputCursor currentTestOutcomeResultCursor = descendantElements(currentTestTags);
      advanceCursor(currentTestOutcomeResultCursor);
      String status = findAttributeValue(currentTestOutcomeResultCursor, "status");
      String category = null;
      if (null != findAttributeValue(currentTestOutcomeResultCursor, "category")) {
        category = findAttributeValue(currentTestOutcomeResultCursor, "category");
      }
      LOG.debug("---status : {}", status);

      TestStatus executionStatus = TestStatus.computeStatus(status, category);
      nextPosition(currentTestTags);
      detail.setStatus(executionStatus);
      if ((executionStatus == TestStatus.FAILED) || (executionStatus == TestStatus.ERROR)) {
        detail = getMessages(currentTestTags, detail);
      }
      return detail;
    }
    return null;
  }

  private TestCaseDetail getMessages(SMInputCursor currentTestTags, TestCaseDetail detail) {
    if ("testLog".equals(findElementName(currentTestTags))) {
      SMInputCursor currentTestLogStreamsTags = descendantElements(currentTestTags);
      SMEvent streamsTag = nextPosition(currentTestLogStreamsTags);
      if (null != streamsTag) {
        LOG.debug("----streams Tag found : {}", findElementName(currentTestLogStreamsTags));
        if (streamsTag.getEventCode() == SMEvent.START_ELEMENT.getEventCode()) {
          LOG.debug("----Cursor is at <streams> Tag ");
          SMInputCursor currentTestLogStreamTags = descendantElements(currentTestLogStreamsTags);
          parseStreams(detail, currentTestLogStreamTags);
        }
      }
    }
    return detail;
  }

  private void parseStreams(TestCaseDetail detail, SMInputCursor currentTestLogStreamTags) {
    try {
      while (null != nextPosition(currentTestLogStreamTags)) {
        LOG.debug("----Cursor is at <stream> Tag ");
        String streamName = findAttributeValue(currentTestLogStreamTags, "name");
        LOG.debug("----stream name : {}", streamName);
        SMInputCursor currentTestLogStreamSectionsTags = currentTestLogStreamTags.descendantElementCursor().advance()
            .descendantElementCursor().advance().descendantElementCursor();
        while (null != nextPosition(currentTestLogStreamSectionsTags)) {
          SMInputCursor sectionContentsChild = currentTestLogStreamSectionsTags;
          if ("section".equals(findElementName(currentTestLogStreamSectionsTags))) {
            String sectionName = findAttributeValue(currentTestLogStreamSectionsTags, "name");
            LOG.debug("----section name : {}", sectionName);
            sectionContentsChild = currentTestLogStreamSectionsTags.descendantElementCursor().advance().descendantElementCursor().advance();
          }
          if ("text".equals(findElementName(sectionContentsChild))) {
            String message = sectionContentsChild.collectDescendantText();
            LOG.debug("Error Message is : {}", message);
            detail.setErrorMessage(message);
          } else if ("marker".equals(findElementName(sectionContentsChild)) && isAStartElement(sectionContentsChild)) {
            LOG.debug("-------Marker found ! ");
            if ("StackTrace".equals(findAttributeValue(sectionContentsChild, "class"))) {
              SMInputCursor sectionMarkerTextContent = sectionContentsChild.descendantElementCursor().advance().descendantElementCursor()
                  .advance();
              String stackTrace = sectionMarkerTextContent.collectDescendantText();
              LOG.debug("StackTrace is : {}", stackTrace);
              detail.setStackTrace(stackTrace);
            }
          }
        }
      }
    } catch (XMLStreamException e) {
      LOG.error(GALLIO_REPORT_PARSING_ERROR, e);
    }
  }

  private Set<UnitTestReport> createUnitTestsReport(Map<String, TestDescription> testsDescriptionByTestIds) {

    Set<UnitTestReport> result = new HashSet<UnitTestReport>();
    Set<String> testIds = testCaseDetailsByTestIds.keySet();
    // We associate the descriptions with the test details
    List<String> testsToRemove = new ArrayList<String>();
    for (String testId : testIds) {
      TestDescription description = testsDescriptionByTestIds.get(testId);
      TestCaseDetail testCaseDetail = testCaseDetailsByTestIds.get(testId);
      if (description == null) {
        LOG.debug(
            "Test {} is not considered as a testCase in your xml, there should not be any testStep associated, please check your gallio report. Skipping result",
            testId);
        testsToRemove.add(testId);
      } else {
        testCaseDetail.merge(description);
        testCaseDetailsByTestIds.put(testId, testCaseDetail);
      }
    }

    LOG.debug("Tests to be removed {}", testsToRemove.size());
    for (String testToRemove : testsToRemove) {
      testCaseDetailsByTestIds.remove(testToRemove);
    }

    Collection<TestCaseDetail> testCases = testCaseDetailsByTestIds.values();
    Multimap<String, TestCaseDetail> testCaseDetailsBySrcKey = ArrayListMultimap.create();
    for (TestCaseDetail testCaseDetail : testCases) {
      String sourceKey = testCaseDetail.createSourceKey();
      testCaseDetailsBySrcKey.put(sourceKey, testCaseDetail);
    }

    Map<String, UnitTestReport> unitTestsReports = new HashMap<String, UnitTestReport>();
    LOG.debug("testCaseDetails size : {}", String.valueOf(testCaseDetailsByTestIds.size()));

    Set<String> pathKeys = testCaseDetailsBySrcKey.keySet();
    LOG.debug("There are {} different pathKeys", String.valueOf(pathKeys.size()));
    for (String pathKey : pathKeys) {
      // If the Key already exists in the map, we add the details
      if (unitTestsReports.containsKey(pathKey)) {
        UnitTestReport unitTest = unitTestsReports.get(pathKey);
        for (TestCaseDetail testDetail : testCaseDetailsBySrcKey.get(pathKey)) {
          LOG.debug("Adding testDetail {} to the unitTestReport", testDetail.getName());
          unitTest.addDetail(testDetail);
        }
        unitTestsReports.put(pathKey, unitTest);
      } else {
        // Else we create a new report
        UnitTestReport unitTest = new UnitTestReport();
        unitTest.setAssemblyName(testCaseDetailsBySrcKey.get(pathKey).iterator().next().getAssemblyName());
        unitTest.setSourceFile(testCaseDetailsBySrcKey.get(pathKey).iterator().next().getSourceFile());
        LOG.debug("Create new unitTest for path : {}", unitTest.getSourceFile().getPath());
        for (TestCaseDetail testDetail : testCaseDetailsBySrcKey.get(pathKey)) {
          LOG.debug("+ and add details : {}", testDetail.getName());
          unitTest.addDetail(testDetail);
        }
        unitTestsReports.put(pathKey, unitTest);
      }
    }
    result.addAll(unitTestsReports.values());

    LOG.debug("The result Set contains " + result.size() + " report(s)");

    return result;
  }
}