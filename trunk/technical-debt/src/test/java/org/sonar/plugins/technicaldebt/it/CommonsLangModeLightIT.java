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

package org.sonar.plugins.technicaldebt.it;

import org.junit.Test;
import org.junit.BeforeClass;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.ResourceQuery;
import org.sonar.wsclient.services.Measure;
import static junit.framework.Assert.assertNull;

public class CommonsLangModeLightIT {

  private static Sonar sonar;
  private static final String PROJECT_STRUTS = "commons-lang:commons-lang";
  private static final String FILE_ACTION = "commons-lang:commons-lang:org.apache.commons.lang.CharRange ";
  private static final String PACKAGE_ACTION = "commons-lang:commons-lang:org.apache.commons.lang";

  @BeforeClass
  public static void buildServer() {
    sonar = Sonar.create("http://localhost:9000");
  }

  @Test
  public void strutsIsAnalyzed() {
    assertThat(sonar.find(new ResourceQuery(PROJECT_STRUTS)).getName(), is("Commons Lang"));
    assertThat(sonar.find(new ResourceQuery(PROJECT_STRUTS)).getVersion(), is("2.5"));
    assertThat(sonar.find(new ResourceQuery(PACKAGE_ACTION)).getName(), is("org.apache.commons.lang"));
  }

  @Test
  public void projectsMetrics() {
    assertThat(getProjectMeasure("technical_debt").getValue(), anyOf(is(39431.3), is(39743.8)));
    assertThat(getProjectMeasure("technical_debt_ratio").getValue(), anyOf(is(10.2), is(10.1)));
    assertThat(getProjectMeasure("technical_debt_days").getValue(), anyOf(is(78.9), is(79.5)));
    assertThat(getProjectMeasure("technical_debt_repart").getData(), anyOf(
      is("Complexity=55.39;Design=6.34;Duplication=10.77;Violations=27.48"),
      is("Complexity=53.54;Design=6.29;Duplication=12.89;Violations=27.26")));
  }

  @Test
  public void packagesMetrics() {
    assertThat(getPackageMeasure("technical_debt").getValue(), anyOf(is(11856.3), is(11356.3)));
    assertThat(getPackageMeasure("technical_debt_ratio").getValue(), anyOf(is(8.6), is(8.7)));
    assertThat(getPackageMeasure("technical_debt_days").getValue(), anyOf(is(23.7), is(22.7)));
    assertThat(getPackageMeasure("technical_debt_repart").getData(), anyOf(
      is("Complexity=68.26;Duplication=9.48;Violations=22.24"),
      is("Complexity=71.27;Duplication=5.5;Violations=23.22")));
  }

  @Test
  public void filesMetrics() {
    assertThat(getFileMeasure("technical_debt").getValue(), is(81.3));
    assertThat(getFileMeasure("technical_debt_ratio").getValue(), is(2.8));
    assertNull(getFileMeasure("technical_debt_days"));
    assertThat(getFileMeasure("technical_debt_repart").getData(), is("Complexity=38.46;Violations=61.53"));
  }

  private Measure getFileMeasure(String metricKey) {
    return sonar.find(ResourceQuery.createForMetrics(FILE_ACTION, metricKey)).getMeasure(metricKey);
  }

  private Measure getPackageMeasure(String metricKey) {
    return sonar.find(ResourceQuery.createForMetrics(PACKAGE_ACTION, metricKey)).getMeasure(metricKey);
  }

  private Measure getProjectMeasure(String metricKey) {
    return sonar.find(ResourceQuery.createForMetrics(PROJECT_STRUTS, metricKey)).getMeasure(metricKey);
  }
}