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
package org.sonar.plugins.jtest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.metrics.CoreMetrics;
import org.sonar.plugins.jtest.JtestXmlProcessor;

public class JtestXmlProcessorTest {

  private ProjectContext context;

  @Before
  public void before() throws Exception {
    File xmlReport = new File(getClass().getResource("/org/sonar/plugins/jtest/JtestXmlProcessorTest/report.xml").toURI());
    context = mock(ProjectContext.class);
    JtestXmlProcessor processor = new JtestXmlProcessor(xmlReport, context);
    processor.process();
  }

  @Test
  @Ignore
  public void shouldGenerateProjectMeasures() {
    verify(context).addMeasure(CoreMetrics.COVERAGE, 66.0);
  }

  @Test
  @Ignore
  public void shouldGeneratePackageMeasures() {
    verify(context).addMeasure(Java.newPackage(""), CoreMetrics.COVERAGE, 40.0);
    verify(context).addMeasure(Java.newPackage("org.sonar.plugins.emma"), CoreMetrics.COVERAGE, 67.0);
    verify(context).addMeasure(Java.newPackage("org.sonar.plugins.gaudin"), CoreMetrics.COVERAGE, 45.0);
  }

  @Test
  @Ignore
  public void shouldGenerateClassMeasures() {
    verify(context).addMeasure(Java.newClass("ClassOnDefaultPackage"),
        CoreMetrics.COVERAGE, 35.0);
    verify(context).addMeasure(Java.newClass("org.sonar.plugins.gaudin.EmmaMavenPluginHandler"),
      CoreMetrics.COVERAGE, 82.0);
    verify(context).addMeasure(Java.newClass("org.sonar.plugins.emma.EmmaMavenPluginHandler"),
      CoreMetrics.COVERAGE, 82.0);
  }
}
