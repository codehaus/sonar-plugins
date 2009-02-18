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
import org.sonar.plugins.api.maven.JavaPackage;
import org.sonar.plugins.api.maven.MavenCollectorUtils;
import org.sonar.plugins.api.maven.ProjectAnalysis;
import org.sonar.plugins.api.maven.JavaClass;
import org.sonar.plugins.api.maven.xml.XmlParserException;
import org.sonar.plugins.api.maven.xml.XpathParser;
import org.sonar.plugins.api.metrics.CoreMetrics;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.text.ParseException;
import java.util.Locale;

public class EmmaXmlProcessor {

  private final File xmlReport;

  private final ProjectAnalysis analysis;

  private final XpathParser parser;

  public EmmaXmlProcessor(File xmlReport, ProjectAnalysis analysis) {
    this.xmlReport = xmlReport;
    this.analysis = analysis;
    parser = new XpathParser();
  }

  protected void process() {
    try {
      if (xmlReport != null) {
        parser.parse(xmlReport);
        collectProjectMeasures(analysis);
        collectPackageMeasures(analysis);
      }

    } catch (ParseException e) {
      throw new XmlParserException(e);
    }
  }

  private void collectProjectMeasures(ProjectAnalysis analysis) throws ParseException {
    String emmaCoverageLineValue = parser.executeXPath("/report/data/all/coverage[@type='line, %']/@value");
    double lineRate = extractEmmaPercentageNumber(emmaCoverageLineValue);
    analysis.addMeasure(CoreMetrics.CODE_COVERAGE, lineRate);
  }

  private void collectPackageMeasures(ProjectAnalysis analysis) throws ParseException {
    NodeList packages = parser.executeXPathNodeList("//package");
    for (int i = 0; i < packages.getLength(); i++) {
      Element packageElement = (Element) packages.item(i);
      String packageName = packageElement.getAttribute("name");
      String packageCoverage = parser.executeXPath(packageElement, "coverage[@type='line, %']/@value");
      analysis.addMeasure(new JavaPackage(packageName), CoreMetrics.CODE_COVERAGE, extractEmmaPercentageNumber(packageCoverage));

      collectClassMeasures(packageElement, packageName, analysis);
    }
  }

  private void collectClassMeasures(Element packageElement, String packageName, ProjectAnalysis analysis) throws ParseException {
    NodeList classes = parser.executeXPathNodeList(packageElement, "srcfile/class");
    for (int i = 0; i < classes.getLength(); i++) {
      Element classElement = (Element) classes.item(i);
      String className = classElement.getAttribute("name");
      String classCoverage = parser.executeXPath(classElement, "coverage[@type='line, %']/@value");
      analysis.addMeasure(new JavaClass(packageName + "." + className, false, false), CoreMetrics.CODE_COVERAGE,
        extractEmmaPercentageNumber(classCoverage));
    }
  }

  private double extractEmmaPercentageNumber(String emmaCoverageLineValue) throws ParseException {
    String extractedStringValue = StringUtils.substringBefore(emmaCoverageLineValue, " ");
    double doubleCoverage = MavenCollectorUtils.parseNumber(extractedStringValue, Locale.ENGLISH);
    return MavenCollectorUtils.scaleValue(doubleCoverage);
  }
}