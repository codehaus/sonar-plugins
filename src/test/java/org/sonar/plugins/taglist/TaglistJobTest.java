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

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sonar.commons.Languages;
import org.sonar.commons.Metric;
import org.sonar.commons.resources.Measure;
import org.sonar.plugins.api.Java;
import org.sonar.plugins.api.jobs.JobContext;

public class TaglistJobTest {

	private JobContext jobContext;
	private TaglistJob job;

	@Before
	public void setUp() throws Exception {
		job = new TaglistJob(new Languages(new Java()));
		jobContext = mock(JobContext.class);

		List<Measure> measures = new ArrayList<Measure>();
		measures.add(new Measure(new Metric("TODO"), 1.0));
		measures.add(new Measure(new Metric("TODO"), 2.0));

		when(jobContext.getChildrenMeasures((Metric) anyObject())).thenReturn(measures);
	}

	@Test
	public void testExecute() {
		job.execute(jobContext);
		verify(jobContext, times(6)).addMeasure((Metric) anyObject(), eq(new Double(3)));
	}

}
