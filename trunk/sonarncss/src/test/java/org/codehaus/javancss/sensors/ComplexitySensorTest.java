package org.codehaus.javancss.sensors;

import static org.codehaus.javancss.JavaNcssUtils.getFile;
import static org.junit.Assert.assertEquals;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.entities.Resource;
import org.codehaus.javancss.entities.Resource.Type;
import org.junit.Test;

public class ComplexitySensorTest {

	@Test
	public void testNoBranches() {
		Resource res = JavaNcss.analyze(getFile("/branches/NoBranches.java"));
		assertEquals(3, res.measures.getComplexity());
	}

	@Test
	public void testSimpleBranches() {
		Resource res = JavaNcss.analyze(getFile("/branches/SimpleBranches.java"));
		assertEquals(15, res.measures.getComplexity());
		
		Resource simpleSwitch = res.find("simpleSwitch()", Type.METHOD);
		assertEquals(3, simpleSwitch.measures.getComplexity());		
	}

	@Test
	public void testInstanceAndStaticInitBlocks() {
		Resource res = JavaNcss.analyze(getFile("/complexity/InstanceAndStaticInitBlocks.java"));
		assertEquals(2, res.measures.getComplexity());
	}
}
