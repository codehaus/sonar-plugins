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
package org.sonar.plugins.greenpepper;

import org.sonar.api.utils.XpathParser;
import org.sonar.api.utils.XmlParserException;
import org.sonar.api.utils.ParsingUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;

public class GreenPepperReportsParser {

  public final static GreenPepperReport parseReports(File reportsDir) {
    GreenPepperReport testsResult = new GreenPepperReport();
    File[] reports = getReports(reportsDir);
    for (File report : reports) {
      testsResult.addGreenPepperReport(parseReport(report));
    }
    return testsResult;
  }

  public final static GreenPepperReport parseReport(File report) {
    try {
      XpathParser parser = new XpathParser();
      parser.parse(report);
      GreenPepperReport testsResult = new GreenPepperReport();
      String testsSuccess = parser.executeXPath("/documents/document/statistics/success");
      testsResult.setTestsSuccess((int) ParsingUtils.parseNumber(testsSuccess));
      String testsError = parser.executeXPath("/documents/document/statistics/error");
      testsResult.setTestsError((int) ParsingUtils.parseNumber(testsError));
      String testsFailure = parser.executeXPath("/documents/document/statistics/failure");
      testsResult.setTestsFailure((int) ParsingUtils.parseNumber(testsFailure));
      String testsIgnored = parser.executeXPath("/documents/document/statistics/ignored");
      testsResult.setTestsIgnored((int) ParsingUtils.parseNumber(testsIgnored));
      return testsResult;
    } catch (ParseException e) {
      throw new XmlParserException("Unable to parse GreenPepper report : " + report.getAbsolutePath(), e);
    }
  }

  private static File[] getReports(File dir) {
    if (dir == null || !dir.isDirectory() || !dir.exists()) {
      return new File[0];
    }
    return dir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.startsWith("GreenPepper") && name.endsWith(".xml");
      }
    });
  }
}
