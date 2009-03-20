package org.codehaus.javancss.metrics;

import static org.junit.Assert.assertEquals;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.Resource;
import org.junit.Test;

public class BranchesCounterTest {

	@Test
	public void testNoBranches() {
		Resource res = JavaNcss.analyze("target/test-classes/branches/NoBranches.java");
		assertEquals(0, res.getBranches());
	}

	@Test
	public void testSimpleBranches() {
		Resource res = JavaNcss.analyze("target/test-classes/branches/SimpleBranches.java");
		assertEquals(8, res.getBranches());
	}
}
