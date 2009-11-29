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
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.apache.commons.configuration.Configuration;

import java.util.Arrays;
import java.util.List;

public class AbstractDecoratorTest {
  private AbstractDecorator decorator;

  @Before
  public void init() {
    decorator = new DecoratorImpl();
  }

  @Test
  public void testDependedUpon() {
    assertThat(decorator.dependedUpon().size(), is(1));
  }

  @Test
  public void testDependsUpon() {
    assertThat(decorator.aggregDependsUpon().size(), is(3));
  }

  @Test
  public void testStandardValidLines() {
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES, 233.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
      thenReturn(new Measure(CoreMetrics.NCLOC, 1344.0));

    assertThat(decorator.getValidLines(context), is(1111.0));
  }

  @Test
  public void testNegativeValidLines() {
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES, 1344.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
      thenReturn(new Measure(CoreMetrics.NCLOC, 344.0));

    assertThat(decorator.getValidLines(context), is(1.0));
  }

  @Test
  public void testComputeAxisWeight() {
    String defaultValue = "2.0";
    double otherValue = 2.4;
    Configuration configuration = mock(Configuration.class);

    when(configuration.getDouble(anyString(), eq(Double.valueOf(defaultValue)))).
      thenReturn(otherValue);
    decorator = new DecoratorImpl(configuration, Double.toString(otherValue), defaultValue);
    assertThat(decorator.computeAxisWeight(), is(otherValue));

    when(configuration.getDouble(null, Double.valueOf(defaultValue))).
      thenReturn(Double.valueOf(defaultValue));
    decorator = new DecoratorImpl(configuration, null, defaultValue);
    assertThat(decorator.computeAxisWeight(), is(Double.valueOf(defaultValue)));
  }

  @Test
  public void testSaveMeasure() {
    Metric metric = new Metric("foo");

    DecoratorContext context = mock(DecoratorContext.class);
    Configuration configuration = mock(Configuration.class);
    decorator = new DecoratorImpl(metric, configuration);

    mockMeasure(context, configuration, Resource.QUALIFIER_UNIT_TEST_CLASS, metric, 0.4);
    decorator.saveMeasure(context, 0.4);
    verify(context,never()).saveMeasure(new Measure(metric, 0.4, "1.0"));

    mockMeasure(context, configuration, Resource.QUALIFIER_UNIT_TEST_CLASS, metric, 0.04);
    decorator.saveMeasure(context, 0.04);
    verify(context,never()).saveMeasure(new Measure(metric, 0.04, "1.0"));

    mockMeasure(context, configuration, Resource.QUALIFIER_PACKAGE, metric, 0.4);
    decorator.saveMeasure(context, 0.4);
    verify(context).saveMeasure(new Measure(metric, 0.4, "1.0"));
  }

  private void mockMeasure(DecoratorContext context, Configuration configuration, String qualifier, Metric metric, double value) {
    Resource resource = mock(Resource.class);
    when(context.getResource()).thenReturn(resource);
    when(resource.getQualifier()).thenReturn(qualifier);
    when(context.getMeasure(metric)).thenReturn(new Measure(metric, value));
    when(configuration.getDouble(anyString(), anyDouble())).thenReturn(1.0);

  }


  public class DecoratorImpl extends AbstractDecorator {
    public DecoratorImpl() {
      super(null, null, null, null);
    }

    public DecoratorImpl(Configuration configuration, String axisWeight, String defaultAxisWeight) {
      super(configuration, null, axisWeight, defaultAxisWeight);
    }

    public DecoratorImpl(Metric metric, Configuration configuration) {
      super(configuration, metric, null, "1.0");
    }

    public void decorate(Resource resource, DecoratorContext context) {
    }

    public List<Metric> dependsUpon() {
      return Arrays.asList(new Metric("foo"));
    }

    public boolean shouldExecuteOnProject(Project project) {
      return false;
    }
  }
}
