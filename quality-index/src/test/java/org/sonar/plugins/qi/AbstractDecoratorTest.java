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

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metric.ValueType;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    double otherValue = 2.4;
    Settings settings = new Settings();

    settings.setProperty(Double.toString(otherValue), otherValue);
    decorator = new DecoratorImpl(settings, Double.toString(otherValue));
    assertThat(decorator.computeAxisWeight(), is(otherValue));
  }

  @Test
  public void testSaveMeasure() {
    Metric metric = new Metric.Builder("foo", "Foo", ValueType.INT).create();

    DecoratorContext context = mock(DecoratorContext.class);
    Settings settings = new Settings();
    decorator = new DecoratorImpl(metric, settings);

    mockMeasure(context, settings, Qualifiers.UNIT_TEST_FILE, metric, 0.4);
    decorator.saveMeasure(context, 0.4);
    verify(context, never()).saveMeasure(new Measure(metric, 0.4, "1.0"));

    mockMeasure(context, settings, Qualifiers.UNIT_TEST_FILE, metric, 0.04);
    decorator.saveMeasure(context, 0.04);
    verify(context, never()).saveMeasure(new Measure(metric, 0.04, "1.0"));

    mockMeasure(context, settings, Qualifiers.PACKAGE, metric, 0.4);
    decorator.saveMeasure(context, 0.4);
    verify(context).saveMeasure(new Measure(metric, 0.4, "1.0"));
  }

  private void mockMeasure(DecoratorContext context, Settings settings, String qualifier, Metric metric, double value) {
    Resource resource = mock(Resource.class);
    when(context.getResource()).thenReturn(resource);
    when(resource.getQualifier()).thenReturn(qualifier);
    when(context.getMeasure(metric)).thenReturn(new Measure(metric, value));
    settings.setProperty("fooAxis", 1.0);
  }

  public class DecoratorImpl extends AbstractDecorator {
    public DecoratorImpl() {
      super(null, null, null);
    }

    public DecoratorImpl(Settings settings, String axisWeight) {
      super(settings, null, axisWeight);
    }

    public DecoratorImpl(Metric metric, Settings settings) {
      super(settings, metric, "fooAxis");
    }

    public void decorate(Resource resource, DecoratorContext context) {
    }

    public List<Metric> dependsUpon() {
      return Arrays.asList(new Metric.Builder("foo", "Foo", ValueType.INT).create());
    }

    public boolean shouldExecuteOnProject(Project project) {
      return false;
    }
  }
}
