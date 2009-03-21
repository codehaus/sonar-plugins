package org.codehaus.javancss.metrics;

import static org.codehaus.javancss.JavaNcssUtils.getFile;
import static org.junit.Assert.assertEquals;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.entities.Resource;
import org.codehaus.javancss.entities.Resource.Type;
import org.junit.Test;

public class ComplexityCounterTest {

	@Test
	public void testNoBranches() {
		Resource res = JavaNcss.analyze(getFile("/branches/NoBranches.java"));
		assertEquals(3, res.getComplexity());
	}

	@Test
	public void testSimpleBranches() {
		Resource res = JavaNcss.analyze(getFile("/branches/SimpleBranches.java"));
		assertEquals(15, res.getComplexity());
		
		Resource simpleSwitch = res.find("simpleSwitch()", Type.METHOD);
		assertEquals(3, simpleSwitch.getComplexity());		
	}

	@Test
	public void testInstanceAndStaticInitBlocks() {
		Resource res = JavaNcss.analyze(getFile("/complexity/InstanceAndStaticInitBlocks.java"));
		assertEquals(2, res.getComplexity());
	}
}
