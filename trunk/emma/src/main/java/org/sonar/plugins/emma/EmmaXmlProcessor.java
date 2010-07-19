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

import org.apache.commons.lang.StringUtils;
import org.codehaus.staxmate.in.SMFilterFactory;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.JavaPackage;
import org.sonar.api.utils.ParsingUtils;
import org.sonar.api.utils.StaxParser;
import org.sonar.api.utils.XmlParserException;

import java.io.File;
import java.text.ParseException;
import java.util.Locale;
import javax.xml.stream.XMLStreamException;

public class EmmaXmlProcessor {

  private final static String EMMA_DEFAULT_PACKAGE = "default package";

  private final File xmlReport;

  private final SensorContext context;

  public EmmaXmlProcessor(File xmlReport, SensorContext context) {
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

  private double extractEmmaPercentageNumber(String emmaCoverageLineValue) throws ParseException {
    String extractedStringValue = StringUtils.substringBefore(emmaCoverageLineValue, " ");
    double doubleCoverage = ParsingUtils.parseNumber(extractedStringValue, Locale.ENGLISH);
    return ParsingUtils.scaleValue(doubleCoverage);
  }

  private void parse(File xml) throws Exception {
    StaxParser parser = new StaxParser(new StaxParser.XmlStreamHandler() {
      public void stream(SMHierarchicCursor rootCursor) throws XMLStreamException {
        SMInputCursor all = rootCursor.advance().descendantElementCursor("all");
        collectProjectMeasures(all.advance());
      }
    });
    parser.parse(xml);
  }

  private void collectProjectMeasures(SMInputCursor allCursor) throws XMLStreamException {
    SMInputCursor packagesCursor = findLineRateMeasure(allCursor, new LineRateMeasureHandler() {
      public void handleMeasure(String resourceName, double coverage) {
        context.saveMeasure(CoreMetrics.COVERAGE, coverage);
      }
    }, null);
    packagesCursor.setFilter(SMFilterFactory.getElementOnlyFilter("package"));
    collectPackageMeasures(packagesCursor);
  }

  private void collectPackageMeasures(SMInputCursor packageCursor) throws XMLStreamException {
    while (packageCursor.getNext() != null) {
      String currentPackageName = packageCursor.getAttrValue("name");
      currentPackageName = currentPackageName.equals(EMMA_DEFAULT_PACKAGE) ? JavaPackage.DEFAULT_PACKAGE_NAME : currentPackageName;
      SMInputCursor srcFileCursor = findLineRateMeasure(packageCursor, new LineRateMeasureHandler() {
        public void handleMeasure(String resourceName, double coverage) {
          context.saveMeasure(new JavaPackage(resourceName), CoreMetrics.COVERAGE, coverage);
        }
      }, currentPackageName);

      srcFileCursor.setFilter(SMFilterFactory.getElementOnlyFilter("srcfile"));
      collectFileMeasures(srcFileCursor, currentPackageName);
    }
  }

  private void collectFileMeasures(SMInputCursor srcFileCursor, String packageName) throws XMLStreamException {
    while (srcFileCursor.getNext() != null) {
      String filename = StringUtils.substringBeforeLast(srcFileCursor.getAttrValue("name"), ".java");
      String classname = packageName.equals(JavaPackage.DEFAULT_PACKAGE_NAME) ? filename : packageName + "." + filename;
      findLineRateMeasure(srcFileCursor, new LineRateMeasureHandler() {
        public void handleMeasure(String resourceName, double coverage) {
          context.saveMeasure(new JavaFile(resourceName), CoreMetrics.COVERAGE, coverage);
        }
      }, classname);
    }
  }

  private SMInputCursor findLineRateMeasure(SMInputCursor cursor, LineRateMeasureHandler handler, String resourceName) throws XMLStreamException {
    SMInputCursor coverage = cursor.childElementCursor("coverage");
    boolean coverageValFound = false;
    while (coverage.getNext() != null) {
      String typeAttr = coverage.getAttrValue("type");
      if (typeAttr.equals("line, %")) {
        try {
          handler.handleMeasure(resourceName, extractEmmaPercentageNumber(coverage.getAttrValue("value")));
        } catch (ParseException e) {
          throw new XMLStreamException(e);
        }
        coverageValFound = true;
        break;
      }
    }
    if (!coverageValFound) {
      throw new XMLStreamException("Unable to find coverage element in XML file");
    }
    return coverage;
  }

  private interface LineRateMeasureHandler {
    void handleMeasure(String resourceName, double coverage);
  }

}