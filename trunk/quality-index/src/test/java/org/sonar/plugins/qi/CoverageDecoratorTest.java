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
package org.sonar.plugins.qi;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;

public class CoverageDecoratorTest {
  @Test
  public void testDependsUpon() {
    CoverageDecorator decorator = new CoverageDecorator(null);
    assertThat(decorator.dependsUpon().size(), is(2));
  }

  @Test
  public void testCoverComputation() {
    CoverageDecorator decorator = new CoverageDecorator(null);

    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getMeasure(CoreMetrics.COVERAGE)).
        thenReturn(new Measure(CoreMetrics.COVERAGE, 65.4));
    assertThat(decorator.computeCoverage(context), is(0.346));

    when(context.getMeasure(CoreMetrics.COVERAGE)).
        thenReturn(null);
    assertThat(decorator.computeCoverage(context), is(1.0));

  }
}
