/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
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

import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.ResourceQuery;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StrutsIT {

  private static Sonar sonar;
  private static final String PROJECT_STRUTS = "org.apache.struts:struts-parent";
  private static final String MODULE_CORE = "org.apache.struts:struts-core";
  private static final String FILE_ACTION = "org.apache.struts:struts-core:org.apache.struts.action.Action";
  private static final String PACKAGE_ACTION = "org.apache.struts:struts-core:org.apache.struts.action";

  @BeforeClass
  public static void buildServer() {
    sonar = Sonar.create("http://localhost:9000");
  }

  @Test
  public void strutsIsAnalyzed() {
    assertThat(sonar.find(new ResourceQuery(PROJECT_STRUTS)).getName(), is("Struts"));
    assertThat(sonar.find(new ResourceQuery(PROJECT_STRUTS)).getVersion(), is("1.3.9"));
    assertThat(sonar.find(new ResourceQuery(MODULE_CORE)).getName(), is("Struts Core"));
    assertThat(sonar.find(new ResourceQuery(PACKAGE_ACTION)).getName(), is("org.apache.struts.action"));
  }

  @Test
  public void projectsMetrics() {
    assertThat(getProjectMeasure("technical_debt_repart").getData(),
        is("Comments=4.68;Complexity=12.63;Coverage=32.63;Design=3.49;Duplication=29.87;Violations=16.66"));

    assertThat(getProjectMeasure("technical_debt").getValue(), is(278655.3));
    assertThat(getProjectMeasure("technical_debt_ratio").getValue(), is(22.7));
    assertThat(getProjectMeasure("technical_debt_days").getValue(), is(557.3));
  }

  @Test
  public void modulesMetrics() {
    assertThat(getCoreModuleMeasure("technical_debt_repart").getData(),
        is("Comments=10.15;Complexity=20.72;Coverage=34.71;Design=14.45;Duplication=1.2;Violations=18.74"));
    assertThat(getCoreModuleMeasure("technical_debt").getValue(), is(62265.1));
    assertThat(getCoreModuleMeasure("technical_debt_ratio").getValue(), is(17.4));
    assertThat(getCoreModuleMeasure("technical_debt_days").getValue(), is(124.5));
  }

  @Test
  public void packagesMetrics() {
    assertThat(getPackageMeasure("technical_debt_repart").getData(),
        is("Comments=0.14;Complexity=26.28;Coverage=50.46;Duplication=2.88;Violations=20.23"));
    assertThat(getPackageMeasure("technical_debt").getValue(), is(8680.3));
    assertThat(getPackageMeasure("technical_debt_ratio").getValue(), is(10.1));
    assertThat(getPackageMeasure("technical_debt_days").getValue(), is(17.4));
  }

  @Test
  public void filesMetrics() {
    assertThat(getFileMeasure("technical_debt").getValue(), is(662.5));
    assertThat(getFileMeasure("technical_debt_ratio").getValue(), is(26.5));
    assertThat(getFileMeasure("technical_debt_days").getValue(), is(1.3));
    assertThat(getFileMeasure("technical_debt_repart").getData(), is("Coverage=75.47;Violations=24.52"));
  }

  private Measure getFileMeasure(String metricKey) {
    return sonar.find(ResourceQuery.createForMetrics(FILE_ACTION, metricKey)).getMeasure(metricKey);
  }

  private Measure getPackageMeasure(String metricKey) {
    return sonar.find(ResourceQuery.createForMetrics(PACKAGE_ACTION, metricKey)).getMeasure(metricKey);
  }

  private Measure getCoreModuleMeasure(String metricKey) {
    return sonar.find(ResourceQuery.createForMetrics(MODULE_CORE, metricKey)).getMeasure(metricKey);
  }

  private Measure getProjectMeasure(String metricKey) {
    return sonar.find(ResourceQuery.createForMetrics(PROJECT_STRUTS, metricKey)).getMeasure(metricKey);
  }
}
