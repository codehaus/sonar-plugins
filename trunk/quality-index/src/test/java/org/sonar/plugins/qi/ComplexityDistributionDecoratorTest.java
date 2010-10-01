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

import com.google.common.collect.Lists;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.Measure;

import java.util.Collection;

public class ComplexityDistributionDecoratorTest {

  @Test
  public void testDependedUpon() {
    ComplexityDistributionDecorator decorator = new ComplexityDistributionDecorator();
    assertThat(decorator.dependedUpon(), is(QIMetrics.QI_COMPLEX_DISTRIBUTION));
  }


  @Test
  public void testComputeComplexityDistribution() {
    DecoratorContext context = mock(DecoratorContext.class);

    Collection<Measure> measures = Lists.newArrayList(
        new Measure(QIMetrics.QI_COMPLEX_DISTRIBUTION, "1=0;2=0;10=0;20=1;30=4"),
        new Measure(QIMetrics.QI_COMPLEX_DISTRIBUTION, "1=3;2=2;10=0;20=9;30=4"),
        new Measure(QIMetrics.QI_COMPLEX_DISTRIBUTION, "1=0;2=0;10=0;20=1;30=4")
    );
    when(context.getChildrenMeasures(QIMetrics.QI_COMPLEX_DISTRIBUTION)).
        thenReturn(measures);

    ComplexityDistributionDecorator decorator = new ComplexityDistributionDecorator();

    Measure measure = decorator.computeComplexityDistribution(context, QIPlugin.COMPLEXITY_BOTTOM_LIMITS);
    Measure measure2 = new Measure(QIMetrics.QI_COMPLEX_DISTRIBUTION, "1=3;2=2;10=0;20=11;30=12");

    assertThat(measure.getData(), is(measure2.getData()));

  }
}
