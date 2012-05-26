/*
 * Sonar Thucydides Plugin
 * Copyright (C) 2012 Patroklos PAPAPETROU
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

package org.sonar.plugins.thucydides;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author patros
 */
public class ThucydidesReportTest {
  private final ThucydidesReport thucydidesReport = new ThucydidesReport();
  @Test
  public void testInitialization() {
    
    assertThat(thucydidesReport.getTests(), equalTo(0));
    assertThat(thucydidesReport.getDuration(), equalTo(0L));
    assertThat(thucydidesReport.getFailed(), equalTo(0));
    assertThat(thucydidesReport.getPassed(), equalTo(0));
    assertThat(thucydidesReport.getPending(), equalTo(0));
  }

  @Test
  public void testGetSuccesRate() {
    thucydidesReport.setTests(10);
    thucydidesReport.setPassed(3);
    double expResult = 30.0;
    double result = thucydidesReport.getSuccesRate();
    assertThat(result, equalTo(expResult));
  }
}
