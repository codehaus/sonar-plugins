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
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.JavaPackage;

import java.io.File;

public class EmmaXmlProcessorTest {

  private SensorContext context;

  @Before
  public void before() throws Exception {
    File xmlReport = new File(getClass().getResource("/org/sonar/plugins/emma/EmmaXmlProcessorTest/coverage.xml").toURI());
    context = mock(SensorContext.class);
    EmmaXmlProcessor processor = new EmmaXmlProcessor(xmlReport, context);
    processor.process();
  }

  @Test
  public void shouldGenerateProjectMeasures() {
    verify(context).saveMeasure(CoreMetrics.COVERAGE, 66.0);
  }

  @Test
  public void shouldGeneratePackageMeasures() {
    verify(context).saveMeasure(new JavaPackage(""), CoreMetrics.COVERAGE, 40.0);
    verify(context).saveMeasure(new JavaPackage("org.sonar.plugins.emma"), CoreMetrics.COVERAGE, 67.0);
    verify(context).saveMeasure(new JavaPackage("org.sonar.plugins.gaudin"), CoreMetrics.COVERAGE, 45.0);
  }

  @Test
  public void shouldGenerateClassMeasures() {
    verify(context).saveMeasure(new JavaFile("ClassOnDefaultPackage"),
        CoreMetrics.COVERAGE, 35.0);
    verify(context).saveMeasure(new JavaFile("org.sonar.plugins.gaudin.EmmaMavenPluginHandler"),
        CoreMetrics.COVERAGE, 82.0);
    verify(context).saveMeasure(new JavaFile("org.sonar.plugins.emma.EmmaMavenPluginHandler"),
        CoreMetrics.COVERAGE, 82.0);
  }
}
