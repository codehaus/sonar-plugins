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
package org.sonar.plugins.emma;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.sonar.plugins.api.maven.JavaClass;
import org.sonar.plugins.api.maven.JavaPackage;
import org.sonar.plugins.api.maven.ProjectAnalysis;
import org.sonar.plugins.api.metrics.CoreMetrics;

import java.io.File;

public class EmmaXmlProcessorTest {

  private ProjectAnalysis analysis;

  @Before
  public void before() throws Exception {
    File xmlReport = new File(getClass().getResource("/org/sonar/plugins/emma/EmmaXmlProcessorTest/coverage.xml").toURI());
    analysis = mock(ProjectAnalysis.class);
    EmmaXmlProcessor processor = new EmmaXmlProcessor(xmlReport, analysis);
    processor.process();
  }

  @Test
  public void shouldGenerateProjectMeasures() {
    verify(analysis).addMeasure(CoreMetrics.CODE_COVERAGE, 66.0);
  }

  @Test
  public void shouldGeneratePackageMeasures() {
    verify(analysis).addMeasure(new JavaPackage("org.sonar.plugins.emma"), CoreMetrics.CODE_COVERAGE, 67.0);
    verify(analysis).addMeasure(new JavaPackage("org.sonar.plugins.gaudin"), CoreMetrics.CODE_COVERAGE, 45.0);
  }

  @Test
  public void shouldGenerateClassMeasures() {
    verify(analysis).addMeasure(new JavaClass("org.sonar.plugins.gaudin.EmmaMavenPluginHandler", false, false),
      CoreMetrics.CODE_COVERAGE, 82.0);
    verify(analysis).addMeasure(new JavaClass("org.sonar.plugins.emma.EmmaMavenPluginHandler", false, false),
      CoreMetrics.CODE_COVERAGE, 82.0);
  }
}
