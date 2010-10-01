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

package org.sonar.plugins.qi;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;

public class ComplexityDecoratorTest {

  @Test
  public void testIOMetrics() {
    ComplexityDecorator decorator = new ComplexityDecorator(null);
    assertThat(decorator.dependedUpon().size(), is(3));
    assertThat(decorator.dependsUpon().size(), is(1));
  }

  @Test
  public void testComplexityFactorComputation() {
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getMeasure(QIMetrics.QI_COMPLEX_DISTRIBUTION)).
        thenReturn(null);

    ComplexityDecorator decorator = new ComplexityDecorator(null);
    assertThat(decorator.computeComplexityFactor(context), is(0.0));
    when(context.getMeasure(QIMetrics.QI_COMPLEX_DISTRIBUTION)).
        thenReturn(new Measure(QIMetrics.QI_COMPLEX_DISTRIBUTION, "2=2;10=4;20=12;30=2"));
    assertThat(decorator.computeComplexityFactor(context), is(50.0));

    when(context.getMeasure(QIMetrics.QI_COMPLEX_DISTRIBUTION)).
        thenReturn(new Measure(QIMetrics.QI_COMPLEX_DISTRIBUTION, "2=0;10=0;20=0;30=12"));
    assertThat(decorator.computeComplexityFactor(context), is(100.0));
  }

  @Test
  public void testComputeComplexity() {
    DecoratorContext context = mock(DecoratorContext.class);

    ComplexityDecorator decorator = new ComplexityDecorator(null);
    assertThat(decorator.computeComplexityFactor(context), is(0.0));
    when(context.getMeasure(QIMetrics.QI_COMPLEX_DISTRIBUTION)).
        thenReturn(new Measure(QIMetrics.QI_COMPLEX_DISTRIBUTION, "2=2;10=4;20=12;30=2"));
    assertThat(decorator.computeComplexity(context), is(94.0));
  }

  @Test
  public void testComputeComplexMethodCount() {
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getMeasure(QIMetrics.QI_COMPLEX_DISTRIBUTION)).
        thenReturn(
            null,
            new Measure(QIMetrics.QI_COMPLEX_DISTRIBUTION, "2=0;10=0;20=0;30=12")
        );
    ComplexityDecorator decorator = new ComplexityDecorator(null);
    assertThat(decorator.computeComplexMethodsCount(context), is(0.0));
    assertThat(decorator.computeComplexMethodsCount(context), is(12.0));

  }
}
