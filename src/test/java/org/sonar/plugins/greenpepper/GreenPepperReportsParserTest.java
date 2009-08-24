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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

public class GreenPepperReportsParserTest {

  @Test
  public void testParseReport() throws URISyntaxException {
    File xmlReport = new File(getClass().getResource("/GreenPepper Confluence-GREENPEPPER-Cell decoration.xml")
      .toURI());
    GreenPepperReport report = GreenPepperReportsParser.parseReport(xmlReport);

    assertEquals(14, report.getTests());
    assertEquals(1, report.getSkippedTests());
    assertEquals(0, report.getTestErrors());
    assertEquals(0, report.getTestFailures());
    assertEquals(13, report.getTestsSuccess());
  }

  @Test
  public void testParseReports() throws URISyntaxException {
    File reportsDir = new File(getClass().getResource("/").toURI());
    GreenPepperReport report = GreenPepperReportsParser.parseReports(reportsDir);

    assertEquals(31, report.getTests());
    assertEquals(5, report.getSkippedTests());
    assertEquals(0, report.getTestErrors());
    assertEquals(1, report.getTestFailures());
    assertEquals(25, report.getTestsSuccess());
    assertEquals(25.0 / 26.0, report.getTestSuccessPercentage(), 0.00001);
  }
}
