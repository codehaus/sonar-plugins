package org.codehaus.sonar.plugins.testability.xml;

import static org.codehaus.sonar.plugins.testability.TestabilityMetrics.*;

import java.io.File;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDetail;
import org.codehaus.sonar.plugins.testability.client.model.ViolationCostDetail;
import org.codehaus.sonar.plugins.testability.measurers.MethodTestabilityCostMeasurer;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMEvent;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.commons.resources.Measure;
import org.sonar.commons.resources.Resource;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.xml.XmlParserException;

public class TestabilityStaxParser {

  private static final String REASON_ATTR = "reason";
  private static final String LINE_ATTR = "line";
  private static final String NEEDS_WORK_ATTR = "needsWork";
  private static final String GOOD_ATTR = "good";
  private static final String EXCELLENT_ATTR = "excellent";
  private static final String OVERALL_ATTR = "overall";
  private static final String LOD_ATTR = "lod";
  private static final String GLOBAL_ATTR = "global";
  private static final String CYCLOMATIC_ATTR = "cyclomatic";
  private static final String CLASS_TAG = "class";
  private static final String METHOD_TAG = "method";
  private static final String COST_TAG = "cost";

  public void parse(File file, ProjectContext context) {
    SMInputFactory inf = new SMInputFactory(XMLInputFactory.newInstance());
    try {
      SMInputCursor cursor = inf.rootElementCursor(file).advance();
      parseTestability(cursor, context);
      cursor.getStreamReader().closeCompletely();
    } catch (XMLStreamException e) {
      throw new XmlParserException(e);
    }
  }

  private void parseTestability(SMInputCursor cursor, ProjectContext context) throws XMLStreamException {
    context.addMeasure(EXCELLENT_CLASSES, StaxMateHelper.getDoubleValue(cursor, EXCELLENT_ATTR));
    context.addMeasure(ACCEPTABLE_CLASSES, StaxMateHelper.getDoubleValue(cursor, GOOD_ATTR));
    context.addMeasure(NEEDSWORK_CLASSES, StaxMateHelper.getDoubleValue(cursor, NEEDS_WORK_ATTR));
    context.addMeasure(TESTABILITY_COST, StaxMateHelper.getDoubleValue(cursor, OVERALL_ATTR));
    parseClasses(cursor, context);
  }

  private void parseClasses(SMInputCursor cursor, ProjectContext context) throws XMLStreamException {
    SMInputCursor classCursor = cursor.descendantElementCursor(CLASS_TAG);
    SMEvent event = null;
    while ((event = classCursor.getNext()) != null) {
      if (event.compareTo(SMEvent.START_ELEMENT) == 0) {
        Resource resource = Java.newClass(StaxMateHelper.getStringValue(classCursor, CLASS_TAG));
        context.addMeasure(resource, new Measure(TESTABILITY_COST, StaxMateHelper.getDoubleValue(classCursor, COST_TAG)));
        MethodTestabilityCostMeasurer measurer = new MethodTestabilityCostMeasurer();
        parseMethods(classCursor, measurer);
        context.addMeasure(resource, measurer.build());
      }
    }
  }

  private void parseMethods(SMInputCursor cursor, MethodTestabilityCostMeasurer measurer) throws XMLStreamException {
    SMInputCursor methodCursor = cursor.descendantElementCursor(METHOD_TAG);
    SMEvent event = null;
    while ((event = methodCursor.getNext()) != null) {
      if (event.compareTo(SMEvent.START_ELEMENT) == 0) {
        addMethodCostToMeasurer(methodCursor, measurer);
        parseViolations(methodCursor, measurer);
      }
    }
  }

  private void addMethodCostToMeasurer(SMInputCursor cursor, MethodTestabilityCostMeasurer measurer) {
    MethodTestabilityCostDetail methodCostDetail = new MethodTestabilityCostDetail();
    methodCostDetail.setCyclomaticComplexity(StaxMateHelper.getIntValue(cursor, CYCLOMATIC_ATTR));
    methodCostDetail.setGlobal(StaxMateHelper.getIntValue(cursor, GLOBAL_ATTR));
    methodCostDetail.setLawOfDemeter(StaxMateHelper.getIntValue(cursor, LOD_ATTR));
    methodCostDetail.setCyclomaticComplexity(StaxMateHelper.getIntValue(cursor, OVERALL_ATTR));
    measurer.addMethodCost(StaxMateHelper.getIntValue(cursor, LINE_ATTR), methodCostDetail);
  }

  private void parseViolations(SMInputCursor cursor, MethodTestabilityCostMeasurer measurer) throws XMLStreamException {
    SMInputCursor violationCursor = cursor.descendantElementCursor(COST_TAG);
    SMEvent event = null;
    while ((event = violationCursor.getNext()) != null) {
      if (event.compareTo(SMEvent.START_ELEMENT) == 0) {
        addViolationCostToMeasurer(violationCursor, measurer);
      }
    }
  }

  private void addViolationCostToMeasurer(SMInputCursor violationCursor, MethodTestabilityCostMeasurer measurer) {
    ViolationCostDetail violationCostDetail = new ViolationCostDetail();
    violationCostDetail.setCyclomaticComplexity(StaxMateHelper.getIntValue(violationCursor, CYCLOMATIC_ATTR));
    violationCostDetail.setGlobal(StaxMateHelper.getIntValue(violationCursor, GLOBAL_ATTR));
    violationCostDetail.setLawOfDemeter(StaxMateHelper.getIntValue(violationCursor, LOD_ATTR));
    violationCostDetail.setOverall(StaxMateHelper.getIntValue(violationCursor, OVERALL_ATTR));
    violationCostDetail.setReason(StaxMateHelper.getStringValue(violationCursor, REASON_ATTR));
    measurer.addViolationCost(StaxMateHelper.getIntValue(violationCursor, LINE_ATTR), violationCostDetail);
  }

}
