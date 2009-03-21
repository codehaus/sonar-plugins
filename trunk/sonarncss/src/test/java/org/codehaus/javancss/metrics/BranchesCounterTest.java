package org.codehaus.javancss.metrics;

import static org.codehaus.javancss.JavaNcssUtils.getFile;
import static org.junit.Assert.assertEquals;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.entities.Resource;
import org.junit.Test;

public class BranchesCounterTest {

	@Test
	public void testNoBranches() {
		Resource res = JavaNcss.analyze(getFile("/branches/NoBranches.java"));
		assertEquals(0, res.getBranches());
	}

	@Test
	public void testSimpleBranches() {
		Resource res = JavaNcss.analyze(getFile("/branches/SimpleBranches.java"));
		assertEquals(8, res.getBranches());
	}
}
