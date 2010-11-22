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

package org.sonar.plugins.web.its;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.ResourceQuery;

public class StandardMeasuresIT {

  private static Sonar sonar;
  private static final String PROJECT = "sonar.web:test";
  private static final String DIR_ROOT = "sonar.web:test:WEB-INF/jsp";
  private static final String FILE = "sonar.web:test:WEB-INF/jsp/admin/buildQueueView.jsp";

  @BeforeClass
  public static void buildServer() {
    sonar = Sonar.create("http://localhost:9000");
  }

  @Test
  public void projectsInfo() {
    assertThat(sonar.find(new ResourceQuery(PROJECT)).getName(), is("Test of Web Plugin"));
    assertThat(sonar.find(new ResourceQuery(PROJECT)).getVersion(), is("1.0"));
  }

  @Test
  public void projectMeasures() {
    assertThat(getProjectMeasure("ncloc").getIntValue(), is(6815));
    assertThat(getProjectMeasure("lines").getIntValue(), is(9252));
    assertThat(getProjectMeasure("files").getIntValue(), is(103));
    //assertThat(getProjectMeasure("directories").getIntValue(), is(2));
    assertNull(getProjectMeasure("functions"));
    assertNull(getProjectMeasure("statements"));
    assertThat(getProjectMeasure("comment_lines_density").getValue(), is(21.9));
    assertThat(getProjectMeasure("comment_lines").getIntValue(), is(1914));
    assertNull(getProjectMeasure("public_api"));
    assertThat(getProjectMeasure("duplicated_lines").getIntValue(), is(108));
    assertThat(getProjectMeasure("duplicated_blocks").getIntValue(), is(4));
    assertThat(getProjectMeasure("duplicated_files").getIntValue(), is(3));
    assertThat(getProjectMeasure("duplicated_lines_density").getValue(), is(1.2));
    assertThat(getProjectMeasure("complexity").getIntValue(), is(391));
    assertNull(getProjectMeasure("function_complexity"));
    assertNull(getProjectMeasure("function_complexity_distribution"));
    assertThat(getProjectMeasure("file_complexity").getValue(), is(3.8));
    assertNull(getProjectMeasure("file_complexity_distribution"));
    assertThat(getProjectMeasure("violations").getIntValue(), is(949));
    assertThat(getProjectMeasure("weighted_violations").getIntValue(), is(1557));
    assertThat(getProjectMeasure("violations_density").getValue(), is(77.2));
  }

  @Test
  public void directoryMeasures() {
    assertThat(getMeasure("ncloc", DIR_ROOT).getIntValue(), is(2877));
    assertThat(getMeasure("violations_density", DIR_ROOT).getValue(), is(80.7));
    assertThat(getMeasure("comment_lines_density", DIR_ROOT).getValue(), is(20.6));
    assertThat(getMeasure("duplicated_lines_density", DIR_ROOT).getValue(), is(1.4));
    assertThat(getMeasure("complexity", DIR_ROOT).getIntValue(), is(150));
  }

  @Test
  public void fileMeasures() {
    assertThat(getFileMeasure("ncloc").getIntValue(), is(311));
    assertThat(getFileMeasure("lines").getIntValue(), is(338));
    assertThat(getFileMeasure("files").getIntValue(), is(1));
    assertNull(getFileMeasure("directories"));
    assertNull(getFileMeasure("functions"));
    assertThat(getFileMeasure("comment_lines_density").getValue(), is(5.8));
    assertThat(getFileMeasure("comment_lines").getIntValue(), is(19));
    assertNull(getFileMeasure("public_api"));
    assertNull(getFileMeasure("duplicated_lines"));
    assertNull(getFileMeasure("duplicated_blocks"));
    assertNull(getFileMeasure("duplicated_files"));
    assertNull(getFileMeasure("duplicated_lines_density"));
    assertNull(getFileMeasure("statements"));
    assertThat(getFileMeasure("complexity").getIntValue(), is(16));
    assertNull(getFileMeasure("function_complexity"));
    assertNull(getFileMeasure("function_complexity_distribution"));
    assertThat(getFileMeasure("file_complexity").getValue(), is(16.0));
    assertNull(getFileMeasure("file_complexity_distribution"));
    assertThat(getFileMeasure("violations").getIntValue(), is(44));
    assertThat(getFileMeasure("weighted_violations").getIntValue(), is(66));
    assertThat(getFileMeasure("violations_density").getValue(), is(78.8));
  }

  private Measure getProjectMeasure(String metricKey) {
    return sonar.find(ResourceQuery.createForMetrics(PROJECT, metricKey)).getMeasure(metricKey);
  }

  private Measure getFileMeasure(String metricKey) {
    return sonar.find(ResourceQuery.createForMetrics(FILE, metricKey)).getMeasure(metricKey);
  }

  private Measure getMeasure(String metricKey, String resourceKey) {
    return sonar.find(ResourceQuery.createForMetrics(resourceKey, metricKey)).getMeasure(metricKey);
  }

}