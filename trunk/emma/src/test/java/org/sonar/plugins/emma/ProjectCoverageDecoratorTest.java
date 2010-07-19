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

import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class ProjectCoverageDecoratorTest {

  @Test
  public void elementsAreSumOfStatementsFunctionsAndAccessors() {
    List<DecoratorContext> children = Arrays.asList(
        mockChildContext(50l, 40l, 10l, 60.0),
        mockChildContext(100l, 100l, 0l, 75.0),
        mockChildContext(80l, 120l, 100l, 90.0)
    );
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getChildren()).thenReturn(children);
    new ProjectCoverageDecorator(null).decorate(new Project(null), context);

    verify(context).saveMeasure(CoreMetrics.COVERAGE, 80.0);
  }

  @Test
  public void accessorsAreOptional() {
    List<DecoratorContext> children = Arrays.asList(
        mockChildContext(50l, 50l, null, 60.0),
        mockChildContext(100l, 100l, null, 75.0)
    );
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getChildren()).thenReturn(children);
    new ProjectCoverageDecorator(null).decorate(new Project(null), context);

    verify(context).saveMeasure(CoreMetrics.COVERAGE, 70.0);
  }

  @Test
  public void coverageCanBeZero() {
    List<DecoratorContext> children = Arrays.asList(
        mockChildContext(50l, 50l, null, 0.0),
        mockChildContext(100l, 100l, null, 0.0)
    );
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getChildren()).thenReturn(children);
    new ProjectCoverageDecorator(null).decorate(new Project(null), context);

    verify(context).saveMeasure(CoreMetrics.COVERAGE, 0.0);
  }

  @Test
  public void childrenCanHaveNoCoverage() {
    List<DecoratorContext> children = Arrays.asList(
        mockChildContext(50l, 50l, null, null),
        mockChildContext(100l, 100l, null, null)
    );
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getChildren()).thenReturn(children);
    new ProjectCoverageDecorator(null).decorate(new Project(null), context);

    verify(context).saveMeasure(CoreMetrics.COVERAGE, 0.0);
  }

  private DecoratorContext mockChildContext(Long statements, Long functions, Long accessors, Double coverage) {
    DecoratorContext context = mock(DecoratorContext.class);
    if (statements != null) {
      when(context.getMeasure(CoreMetrics.STATEMENTS)).thenReturn(new Measure(CoreMetrics.STATEMENTS, statements.doubleValue()));
    }
    if (functions != null) {
      when(context.getMeasure(CoreMetrics.FUNCTIONS)).thenReturn(new Measure(CoreMetrics.FUNCTIONS, functions.doubleValue()));
    }
    if (accessors != null) {
      when(context.getMeasure(CoreMetrics.ACCESSORS)).thenReturn(new Measure(CoreMetrics.ACCESSORS, accessors.doubleValue()));
    }
    if (coverage != null) {
      when(context.getMeasure(CoreMetrics.COVERAGE)).thenReturn(new Measure(CoreMetrics.COVERAGE, coverage));
    }
    return context;
  }
}

