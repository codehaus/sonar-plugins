package org.codehaus.sonar.plugins.testability.xml;

import static org.codehaus.sonar.plugins.testability.TestabilityMetrics.*;

import java.io.File;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMEvent;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.commons.resources.Measure;
import org.sonar.commons.resources.Resource;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.xml.XmlParserException;

public class TestabilityStaxParser {

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
    context.addMeasure(EXCELLENT_CLASSES, StaxMateHelper.getDoubleValue(cursor, "excellent"));
    context.addMeasure(ACCEPTABLE_CLASSES, StaxMateHelper.getDoubleValue(cursor, "good"));
    context.addMeasure(NEEDSWORK_CLASSES, StaxMateHelper.getDoubleValue(cursor, "needsWork"));
    context.addMeasure(TESTABILITY_COST, StaxMateHelper.getDoubleValue(cursor, "overall"));
    parseClasses(cursor, context);
  }

  private void parseClasses(SMInputCursor cursor, ProjectContext context) throws XMLStreamException {
    SMInputCursor classCursor = cursor.descendantElementCursor("class");
    SMEvent event = null;
    while ((event = classCursor.getNext()) != null) {
      if (event.compareTo(SMEvent.START_ELEMENT) == 0) {
        Resource resource = Java.newClass(StaxMateHelper.getStringValue(classCursor, "class"));
        context.addMeasure(resource, new Measure(TESTABILITY_COST, StaxMateHelper.getDoubleValue(cursor, "cost")));
        parseMethods(cursor, context, resource);
      }
    }
  }

  private void parseMethods(SMInputCursor cursor, ProjectContext context, Resource resource) throws XMLStreamException {
    SMInputCursor methodCursor = cursor.descendantElementCursor("method");
    SMEvent event = null;
    while ((event = methodCursor.getNext()) != null) {
      if (event.compareTo(SMEvent.START_ELEMENT) == 0) {
        //
      }
    }
  }

}
