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
import org.junit.Before;
import org.junit.Test;

public class GreenPepperReportTest {

  protected GreenPepperReport report;

  @Before
  public void before() {
    report = new GreenPepperReport();
    report.setTestsError(3);
    report.setTestsFailure(6);
    report.setTestsIgnored(4);
    report.setTestsSuccess(45);
  }

  @Test
  public void testGetTestSuccessPercentage() {
    assertEquals(45.0 / 54.0, report.getTestSuccessPercentage(), 0.00001);
  }

  @Test
  public void testGetTestsCount() {
    assertEquals(58, report.getTests());
  }

  @Test
  public void testAddGreenPepperReport() {
    GreenPepperReport newReport = new GreenPepperReport();
    newReport.setTestsError(1);
    newReport.setTestsFailure(1);
    newReport.setTestsIgnored(1);
    newReport.setTestsSuccess(5);

    report.addGreenPepperReport(newReport);
    assertEquals(66, report.getTests());
  }

}
