package org.codehaus.javancss.sensors;

import static org.codehaus.javancss.JavaNcssUtils.getFile;
import static org.junit.Assert.assertEquals;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.entities.Resource;
import org.junit.Test;

public class BrancheSensorTest {

	@Test
	public void testNoBranches() {
		Resource res = JavaNcss.analyze(getFile("/branches/NoBranches.java"));
		assertEquals(0, res.measures.getBranches());
	}

	@Test
	public void testSimpleBranches() {
		Resource res = JavaNcss.analyze(getFile("/branches/SimpleBranches.java"));
		assertEquals(8, res.measures.getBranches());
	}
}
