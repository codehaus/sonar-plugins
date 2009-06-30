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
package org.sonar.plugins.jtest;

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
import org.sonar.plugins.api.metrics.CoreMetrics;

public class JtestXmlProcessor {
  
  private final static String JTEST_DEFAULT_PACKAGE = "default package";

  private final File xmlReport;

  private final ProjectContext context;

  public JtestXmlProcessor(File xmlReport, ProjectContext context) {
    this.xmlReport = xmlReport;
    this.context = context;
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
 

  private double extractJtestPercentageNumber(String jtestCoverageLineValue) throws ParseException {
    String extractedStringValue = StringUtils.substringBefore(jtestCoverageLineValue, " ");
    double doubleCoverage = MavenCollectorUtils.parseNumber(extractedStringValue, Locale.ENGLISH);
    return MavenCollectorUtils.scaleValue(doubleCoverage);
  }
 
  private void parse(File xml) throws Exception {
    // TODO use org.sonar.plugins.api.maven.xml.StaxParser and StaxMate API instead of 
    // raw Stax API, which is easiers to read and program.
    XMLInputFactory2 xmlFactory = (XMLInputFactory2) XMLInputFactory2.newInstance();
    InputStream input = new FileInputStream(xml);
    XMLStreamReader2 reader = (XMLStreamReader2)xmlFactory.createXMLStreamReader(input);
    XMLStreamReader2 readerPrevious = (XMLStreamReader2)xmlFactory.createXMLStreamReader(input);
    
    boolean allElementProcessedPassed = false;
    String currentPackageName = null;
    while (reader.next() != XMLStreamConstants.END_DOCUMENT) {
	      if (reader.isStartElement()) {
		        String elementElem = reader.getAttributeValue(null, "elem");
		        int elementNumber = reader.getAttributeCount();
		        if (!allElementProcessedPassed && elementElem.equals("Total")) {
		        	collectProjectMeasures(reader);
		        	allElementProcessedPassed = true;
		        } 
		        else if (!elementElem.equals("Total") && elementNumber == 4) {
		        	currentPackageName = readerPrevious.getAttributeValue(null, "elem");
		        	currentPackageName = currentPackageName.equals(JTEST_DEFAULT_PACKAGE) ? Java.DEFAULT_PACKAGE_NAME : currentPackageName;
		        	collectPackageMeasures(readerPrevious, currentPackageName);
		        	if (reader.next() != XMLStreamConstants.END_DOCUMENT) {
		        		String className = reader.getAttributeValue(null, "elem");
		        		collectClassMeasures(reader, currentPackageName, className);
		        		readerPrevious.next();
		        	}
		        } 
		        else {
		          reader.skipElement();
		        }
		        readerPrevious.next();
	      }
    }
    reader.closeCompletely();
    readerPrevious.closeCompletely();
  }

  private void collectProjectMeasures(XMLStreamReader2 reader) throws XMLStreamException, ParseException {
    findLineRateMeasure(reader, new LineRateMeasureHandler() {
      public void handleMeasure(String resourceName, double coverage) {
        context.addMeasure(CoreMetrics.COVERAGE, coverage);
      }
    }, null);
  }
  
  private void collectPackageMeasures(XMLStreamReader2 reader, String resourceName) throws XMLStreamException, ParseException {
    findLineRateMeasure(reader, new LineRateMeasureHandler() {
      public void handleMeasure(String resourceName, double coverage) {
        context.addMeasure(Java.newPackage(resourceName), CoreMetrics.COVERAGE, coverage);
      }
    }, resourceName);
  }
  
  private void collectClassMeasures(XMLStreamReader2 reader, String packageName, String className) throws XMLStreamException, ParseException {
    String classFullName = packageName.equals(Java.DEFAULT_PACKAGE_NAME) ? className : packageName + "." + className;
    findLineRateMeasure(reader, new LineRateMeasureHandler() {
      public void handleMeasure(String resourceName, double coverage) {
        context.addMeasure(Java.newClass(resourceName), CoreMetrics.COVERAGE,coverage);
      }
    }, classFullName);
  }

  private void findLineRateMeasure(XMLStreamReader2 reader, LineRateMeasureHandler handler, String resourceName) throws XMLStreamException, ParseException {
      if (reader.isStartElement() && !(reader.getAttributeValue(null, "val").equals("N/A"))) {
    	  String value = reader.getAttributeValue(null, "val") + " % (" + reader.getAttributeValue(null, "num") + "/" + reader.getAttributeValue(null, "total") + ")";
          double coverage = extractJtestPercentageNumber(value);
          handler.handleMeasure(resourceName, coverage);
      }
      else{
    	  throw new XMLStreamException("Unable to find coverage element in XML file");
      }
  }
  
  private interface LineRateMeasureHandler {
    public void handleMeasure(String resourceName, double coverage);
  }

}