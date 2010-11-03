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

package org.sonar.plugins.taglist;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Project;
import org.sonar.api.measures.Measure;

public class TaglistDecoratorTest {

  private DecoratorContext decoratorContext;
  private Resource resource;
  private TaglistDecorator decorator;

  @Before
  public void setUp() throws Exception {
    decorator = new TaglistDecorator();
    decoratorContext = mock(DecoratorContext.class);
    resource = new Project("foo");
  }

  @Test
  public void testExecute() {

    when(decoratorContext.getChildrenMeasures(TaglistMetrics.TAGS)).
      thenReturn((Collection) Arrays.asList(new Measure(TaglistMetrics.TAGS, 1.0), new Measure(TaglistMetrics.TAGS, 3.0)));
    when(decoratorContext.getChildrenMeasures(TaglistMetrics.MANDATORY_TAGS)).
      thenReturn((Collection) Arrays.asList(new Measure(TaglistMetrics.MANDATORY_TAGS, 8.0), new Measure(TaglistMetrics.MANDATORY_TAGS, 3.0)));
    when(decoratorContext.getChildrenMeasures(TaglistMetrics.OPTIONAL_TAGS)).
      thenReturn((Collection) Arrays.asList(new Measure(TaglistMetrics.OPTIONAL_TAGS, 1.0), new Measure(TaglistMetrics.OPTIONAL_TAGS, 5.0)));

    decorator.decorate(resource, decoratorContext);
    verify(decoratorContext).saveMeasure(eq(new Measure(TaglistMetrics.TAGS ,4.0)));
    verify(decoratorContext).saveMeasure(eq(new Measure(TaglistMetrics.MANDATORY_TAGS ,11.0)));
    verify(decoratorContext).saveMeasure(eq(new Measure(TaglistMetrics.OPTIONAL_TAGS ,6.0)));
  }
}
