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
