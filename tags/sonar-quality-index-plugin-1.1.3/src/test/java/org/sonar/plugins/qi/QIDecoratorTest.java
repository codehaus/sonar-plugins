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
import static org.mockito.Mockito.*;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.JavaFile;

public class QIDecoratorTest {

  @Test
  public void testDependsUpon() {
    QIDecorator decorator = new QIDecorator();
    assertThat(decorator.getRequiredMetrics().size(), is(4));
  }

  @Test
  public void testDependedUpon() {
    QIDecorator decorator = new QIDecorator();
    assertThat(decorator.getGeneratedMetrics().size(), is(1));
  }

  @Test
  public void calculateQualityIndex() {
    QIDecorator decorator = new QIDecorator();
    DecoratorContext context = mock(DecoratorContext.class);
    mockMeasure(context, QIMetrics.QI_COMPLEXITY, 3.0);
    mockMeasure(context, QIMetrics.QI_TEST_COVERAGE, 1.0);
    mockMeasure(context, QIMetrics.QI_CODING_VIOLATIONS, 0.2);
    mockMeasure(context, QIMetrics.QI_STYLE_VIOLATIONS, 2.1);

    decorator.decorate(new JavaFile("Foo"), context);
    verify(context).saveMeasure(QIMetrics.QI_QUALITY_INDEX, 10.0 - 3.0 - 1.0 - 0.2 - 2.1);
  }

  @Test
  public void measuresCanBeOptional() {
    QIDecorator decorator = new QIDecorator();
    DecoratorContext context = mock(DecoratorContext.class);
    mockMeasure(context, QIMetrics.QI_COMPLEXITY, 0.0);
    mockMeasure(context, QIMetrics.QI_TEST_COVERAGE, 1.0);

    decorator.decorate(new JavaFile("Foo"), context);
    verify(context).saveMeasure(QIMetrics.QI_QUALITY_INDEX, 10.0 - 0.0 - 1.0);
  }

  @Test
  public void preventBugIfNegativeValue() {
    QIDecorator decorator = new QIDecorator();
    DecoratorContext context = mock(DecoratorContext.class);
    mockMeasure(context, QIMetrics.QI_COMPLEXITY, 3.0);
    mockMeasure(context, QIMetrics.QI_TEST_COVERAGE, 5.0);
    mockMeasure(context, QIMetrics.QI_CODING_VIOLATIONS, 8.2);
    mockMeasure(context, QIMetrics.QI_STYLE_VIOLATIONS, 6.1);

    decorator.decorate(new JavaFile("Foo"), context);
    verify(context).saveMeasure(QIMetrics.QI_QUALITY_INDEX, 0.0);
  }


  private void mockMeasure(DecoratorContext context, Metric metric, double value) {
    when(context.getMeasure(metric)).thenReturn(new Measure(metric, value));
  }
}
