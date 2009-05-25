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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.sonar.plugins.api.maven.MavenTestCase;
import org.sonar.plugins.api.maven.ProjectContext;
import org.sonar.plugins.api.maven.model.MavenPom;
import org.sonar.plugins.api.metrics.CoreMetrics;

public class EmmaMavenCollectorTest {
  @Test
  public void shouldGetReportPathFromProperty() {
    ProjectContext context = mock(ProjectContext.class);
    MavenPom pom = MavenTestCase.loadPom("/org/sonar/plugins/emma/EmmaMavenCollectorTest/shouldGetReportPathFromProperty/pom.xml");
    new EmmaMavenCollector().collect(pom, context);
    verify(context, atLeastOnce()).addMeasure(eq(CoreMetrics.COVERAGE), anyDouble());
  }

  @Test
  public void shouldNotExecuteEmmaWhenReusingReports() {
    MavenPom pom = MavenTestCase.loadPom("/org/sonar/plugins/emma/EmmaMavenCollectorTest/shouldNotExecuteEmmaWhenReusingReports/pom.xml");
    assertNull(new EmmaMavenCollector().dependsOnMavenPlugin(pom));
  }

  @Test
  public void shouldExecuteEmmaWhenFullDynamicAnalysis() {
    MavenPom pom = MavenTestCase.loadPom("/org/sonar/plugins/emma/EmmaMavenCollectorTest/shouldExecuteEmmaWhenFullDynamicAnalysis/pom.xml");
    assertNotNull(new EmmaMavenCollector().dependsOnMavenPlugin(pom));
  }

  @Test
  public void shouldNotExecuteAtAllWhenStaticAnalysis() {
    MavenPom pom = MavenTestCase.loadPom("/org/sonar/plugins/emma/EmmaMavenCollectorTest/shouldNotExecuteAtAllWhenStaticAnalysis/pom.xml");
    assertNull(new EmmaMavenCollector().dependsOnMavenPlugin(pom));
  }

  @Test
  public void shouldNotFailWhenReportNotFound() {
    MavenPom pom = MavenTestCase.loadPom("/org/sonar/plugins/emma/EmmaMavenCollectorTest/shouldNotFailWhenReportNotFound/pom.xml");
    new EmmaMavenCollector().collect(pom, mock(ProjectContext.class));
  }

  @Test
  public void shouldGetReportPathFromPom() {
    ProjectContext context = mock(ProjectContext.class);
    MavenPom pom = MavenTestCase.loadPom("/org/sonar/plugins/emma/EmmaMavenCollectorTest/shouldGetReportPathFromPom/pom.xml");
    new EmmaMavenCollector().collect(pom, context);
    verify(context, atLeastOnce()).addMeasure(eq(CoreMetrics.COVERAGE), anyDouble());
  }

}
