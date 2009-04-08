package org.sonar.plugins.taglist;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sonar.commons.Metric;

public class TaglistMetricsTest {

	private TaglistMetrics metrics = null;

	@Before
	public void setUp() throws Exception {
		metrics = new TaglistMetrics();
	}

	@Test
	public void testGetMetrics() {
		List<Metric> tagMetrics = metrics.getMetrics();
		assertEquals(tagMetrics.size(), 6);
	}
}
