/*
 * Sonar GreenPepper Plugin
 * Copyright (C) 2009 SonarSource
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

package org.sonar.plugins.greenpepper;

public class GreenPepperReport {

  private int testsSuccess;
  private int testsError;
  private int testsFailure;
  private int testsIgnored;

  double getTestSuccessPercentage() {
    if (getTests() == 0) {
      return 1;
    }
    return (double) testsSuccess / (double) getTestsWithoutIgnored();
  }
  
  int getTestsWithoutIgnored() {
    return getTests() - testsIgnored;
  }

  int getTests() {
    return testsSuccess + testsError + testsFailure + testsIgnored;
  }

  void addGreenPepperReport(GreenPepperReport testReport) {
    testsSuccess += testReport.testsSuccess;
    testsError += testReport.testsError;
    testsFailure += testReport.testsFailure;
    testsIgnored += testReport.testsIgnored;
  }

  public int getTestsSuccess() {
    return testsSuccess;
  }

  public void setTestsSuccess(int testsSuccess) {
    this.testsSuccess = testsSuccess;
  }

  public int getTestErrors() {
    return testsError;
  }

  public void setTestsError(int testsError) {
    this.testsError = testsError;
  }

  public int getTestFailures() {
    return testsFailure;
  }

  public void setTestsFailure(int testsFailure) {
    this.testsFailure = testsFailure;
  }

  public int getSkippedTests() {
    return testsIgnored;
  }

  public void setTestsIgnored(int testsIgnored) {
    this.testsIgnored = testsIgnored;
  }

}
