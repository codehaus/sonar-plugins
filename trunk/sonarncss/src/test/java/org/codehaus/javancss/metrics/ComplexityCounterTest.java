package org.codehaus.javancss.metrics;

import static org.junit.Assert.assertEquals;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.Resource;
import org.junit.Test;

public class ComplexityCounterTest {

	@Test
	public void testNoBranches() {
		Resource res = JavaNcss.analyze("target/test-classes/branches/NoBranches.java");
		assertEquals(3, res.getComplexity());
	}

	@Test
	public void testSimpleBranches() {
		Resource res = JavaNcss.analyze("target/test-classes/branches/SimpleBranches.java");
		assertEquals(15, res.getComplexity());
	}

	@Test
	public void testInstanceAndStaticInitBlocks() {
		Resource res = JavaNcss.analyze("target/test-classes/complexity/InstanceAndStaticInitBlocks.java");
		System.out.println(res);
		assertEquals(2, res.getComplexity());
	}
}
