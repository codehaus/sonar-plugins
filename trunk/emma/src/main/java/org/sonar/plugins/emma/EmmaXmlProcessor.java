/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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
package org.sonar.plugins.emma;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Locale;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.maven.MavenCollectorUtils;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.xml.XmlParserException;
import org.sonar.plugins.api.maven.xml.XpathParser;
import org.sonar.plugins.api.metrics.CoreMetrics;

public class EmmaXmlProcessor {

  private final File xmlReport;

  private final ProjectContext context;

  private final XpathParser parser;

  public EmmaXmlProcessor(File xmlReport, ProjectContext context) {
    this.xmlReport = xmlReport;
    this.context = context;
    parser = new XpathParser();
  }

  protected void process() {
    try {
      if (xmlReport != null) {
        parse(xmlReport);
      }
    } catch (Exception e) {
      throw new XmlParserException(e);
    }
  }

  private double extractEmmaPercentageNumber(String emmaCoverageLineValue) throws ParseException {
    String extractedStringValue = StringUtils.substringBefore(emmaCoverageLineValue, " ");
    double doubleCoverage = MavenCollectorUtils.parseNumber(extractedStringValue, Locale.ENGLISH);
    return MavenCollectorUtils.scaleValue(doubleCoverage);
  }
  
  private void parse(File xml) throws Exception {
    XMLInputFactory2 xmlFactory = (XMLInputFactory2) XMLInputFactory2.newInstance();
    InputStream input = new FileInputStream(xml);
    XMLStreamReader2 reader = (XMLStreamReader2)xmlFactory.createXMLStreamReader(input);

    int event = 0;
    boolean allNodePassed = false;
    String currentPackageName = null;
    while ((event = reader.next()) != XMLStreamConstants.END_DOCUMENT) {
      if (event == XMLStreamConstants.START_ELEMENT) {
        String elementName = reader.getName().getLocalPart();
        if ( !allNodePassed && elementName.equals("all")) {
          collectProjectMeasures(reader);
          allNodePassed = true;
        }
        if (elementName.equals("package")) {
          currentPackageName = reader.getAttributeValue(null, "name");
          collectPackageMeasures(reader, currentPackageName);
        }
        if (elementName.equals("class")) {
          String className = reader.getAttributeValue(null, "name");
          collectClassMeasures(reader, currentPackageName, className);
        }
      }
    }
  }
  
  private void collectProjectMeasures(XMLStreamReader2 reader) throws XMLStreamException, ParseException {
    findLineRateMeasure(reader, new LineRateMeasureHandler() {
      public void handleMeasure(String resourceName, double coverage) {
        context.addMeasure(CoreMetrics.CODE_COVERAGE, coverage);
      }
    }, null);
  }
  
  private void collectPackageMeasures(XMLStreamReader2 reader, String resourceName) throws XMLStreamException, ParseException {
    findLineRateMeasure(reader, new LineRateMeasureHandler() {
      public void handleMeasure(String resourceName, double coverage) {
        context.addMeasure(Java.newPackage(resourceName), CoreMetrics.CODE_COVERAGE, coverage);
      }
    }, resourceName);
  }
  
  private void collectClassMeasures(XMLStreamReader2 reader, String packageName, String className) throws XMLStreamException, ParseException {
    findLineRateMeasure(reader, new LineRateMeasureHandler() {
      public void handleMeasure(String resourceName, double coverage) {
        context.addMeasure(Java.newClass(resourceName), CoreMetrics.CODE_COVERAGE,coverage);
      }
    }, packageName + "." + className);
  }

  private void findLineRateMeasure(XMLStreamReader2 reader, LineRateMeasureHandler handler, String resourceName) throws XMLStreamException, ParseException {
    boolean coverageValFound = false;
    int coverageTagsCounter = 0;
    while (!coverageValFound) {
      int event = reader.nextTag();
      if (event == XMLStreamConstants.START_ELEMENT && reader.getName().getLocalPart().equals("coverage") ) {
        String typeAttr = reader.getAttributeValue(null, "type");
        if ( typeAttr.equals("line, %")) {
          double coverage = extractEmmaPercentageNumber(reader.getAttributeValue(null, "value"));
          handler.handleMeasure(resourceName, coverage);
          coverageValFound = true;
        }
      }
      if (coverageTagsCounter++ == 8) {
        throw new XMLStreamException("Unable to find coverage element in XML file");
      }
    }
  }
  
  private interface LineRateMeasureHandler {
    public void handleMeasure(String resourceName, double coverage);
  }

}