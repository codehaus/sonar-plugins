package org.codehaus.javancss.metrics;

import static org.codehaus.javancss.JavaNcssUtils.getFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.Resource;
import org.junit.Test;

public class JavaDocCounterTest {

	@Test
	public void analyseJavaDocCounter() {
		Resource res = JavaNcss.analyze(getFile("/JavaDocCounter.java"));
		assertEquals(4, res.getJavadocLines());
		assertEquals(2, res.getJavadocBlocks());
		assertFalse(res.hasJavadoc());

		Resource classResource = res.getFirstChild().getFirstChild().getFirstChild();
		assertTrue(classResource.hasJavadoc());
	}
}
