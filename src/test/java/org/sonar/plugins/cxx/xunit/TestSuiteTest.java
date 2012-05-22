/*
 * Sonar Cxx Plugin, open source software quality management tool.
 * Copyright (C) 2010 - 2011, Neticoa SAS France - Tous droits reserves.
 * Author(s) : Franck Bonin, Neticoa SAS France.
 *
 * Sonar Cxx Plugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar Cxx Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar Cxx Plugin; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.cxx.xunit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TestSuiteTest {
  TestSuite suite;
  TestSuite equalSuite;
  TestSuite otherSuite;
  
  @Before
  public void setUp() {
    suite = new TestSuite("key");
    equalSuite = new TestSuite("key");
    otherSuite = new TestSuite("otherkey");
  }

  @Test
  public void suiteDoesntEqualsNull() {
    assert(!suite.equals(null));
  }

  @Test
  public void suiteDoesntEqualsMiscObject() {
    assert(!suite.equals("string"));
  }

  @Test
  public void suiteEqualityIsReflexive() {
    assert(suite.equals(suite));
    assert(otherSuite.equals(otherSuite));
    assert(equalSuite.equals(equalSuite));
  }

  @Test
  public void suiteEqualityWorksAsExpected() {
    assert(suite.equals(equalSuite));
    assert(!suite.equals(otherSuite));
  }

  @Test
  public void suiteHashWorksAsExpected() {
    assert(suite.hashCode() == equalSuite.hashCode());
    assert(suite.hashCode() != otherSuite.hashCode());
  }

  @Test
  public void newBornSuiteShouldHaveVirginStatistics() {
    assertEquals(suite.getTests(), 0);
    assertEquals(suite.getErrors(), 0);
    assertEquals(suite.getFailures(), 0);
    assertEquals(suite.getSkipped(), 0);
    assertEquals(suite.getTime(), 0);
    assertEquals(suite.getDetails(), "<tests-details></tests-details>");
  }

  @Test
  public void addingTestCaseShouldIncrementStatistics() {
    int testBefore = suite.getTests();
    int timeBefore = suite.getTime();

    final int EXEC_TIME = 10;
    suite.addTestCase(new TestCase("name", EXEC_TIME, "status", "stack", "msg"));
    
    assertEquals(suite.getTests(), testBefore + 1);
    assertEquals(suite.getTime(), timeBefore + EXEC_TIME);
  }

  @Test
  public void addingAnErroneousTestCaseShouldIncrementErrorStatistic() {
    int errorsBefore = suite.getErrors();
    TestCase error = mock(TestCase.class);
    when(error.isError()).thenReturn(true);
    
    suite.addTestCase(error);
    
    assertEquals(suite.getErrors(), errorsBefore + 1);
  }

  @Test
  public void addingAFailedestCaseShouldIncrementFailedStatistic() {
    int failedBefore = suite.getFailures();
    TestCase failedTC = mock(TestCase.class);
    when(failedTC.isFailure()).thenReturn(true);
    
    suite.addTestCase(failedTC);
    
    assertEquals(suite.getFailures(), failedBefore + 1);
  }
  
  @Test
  public void addingASkippedTestCaseShouldIncrementSkippedStatistic() {
    int skippedBefore = suite.getSkipped();
    TestCase skippedTC = mock(TestCase.class);
    when(skippedTC.isSkipped()).thenReturn(true);
    
    suite.addTestCase(skippedTC);
    
    assertEquals(suite.getSkipped(), skippedBefore + 1);
  }
}
