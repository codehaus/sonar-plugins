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
package org.sonar.plugins.taglist;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.sonar.commons.resources.Measure;
import org.sonar.api.batch.DecoratorContext;

public class TaglistDecoratorTest {

	private DecoratorContext decoratorContext;
	private TaglistDecorator decorator;

  @Before
	public void setUp() throws Exception {
		decorator = new TaglistDecorator();
		decoratorContext = mock(DecoratorContext.class);
	}

	@Test
	public void testExecute() {
	/*
	  when(decoratorContext.getChildrenMeasures(new MeasureKey(TaglistMetrics.TAGS))).
      thenReturn(Arrays.asList(new Measure(TaglistMetrics.TAGS, 1.0), new Measure(TaglistMetrics.TAGS, 3.0)));
	  when(decoratorContext.getChildrenMeasures(new MeasureKey(TaglistMetrics.MANDATORY_TAGS))).
      thenReturn(Arrays.asList(new Measure(TaglistMetrics.MANDATORY_TAGS, 8.0), new Measure(TaglistMetrics.MANDATORY_TAGS, 3.0)));
	  when(decoratorContext.getChildrenMeasures(new MeasureKey(TaglistMetrics.OPTIONAL_TAGS))).
      thenReturn(Arrays.asList(new Measure(TaglistMetrics.OPTIONAL_TAGS, 1.0), new Measure(TaglistMetrics.OPTIONAL_TAGS, 5.0)));
	 when(decoratorContext.getChildrenMeasures(new MeasureKey(TaglistMetrics.TAGS_DISTRIBUTION))).
     thenReturn(Arrays.asList(new Measure(TaglistMetrics.TAGS_DISTRIBUTION, "test=foo"), new Measure(TaglistMetrics.TAGS_DISTRIBUTION, "foo=bar")));
	  
		decorator.decorate(decoratorContext);
		verify(jobContext, times(1)).addMeasure(eq(TaglistMetrics.TAGS), eq(new Double(4)));
		verify(jobContext, times(1)).addMeasure(eq(TaglistMetrics.MANDATORY_TAGS), eq(new Double(11)));
		verify(jobContext, times(1)).addMeasure(eq(TaglistMetrics.OPTIONAL_TAGS), eq(new Double(6)));
		verify(jobContext, never()).addMeasure(eq(TaglistMetrics.TAGS_DISTRIBUTION), anyDouble());
		*/
	}

}
