package org.codehaus.javancss.metrics;

import static org.junit.Assert.*;

import java.io.File;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.Resource;
import org.junit.Test;

public class JavaDocCounterTest {

	@Test
	public void analyseJavaDocCounter() {
		Resource res = JavaNcss.analyze(new File("target/test-classes/JavaDocCounter.java"));
		assertEquals(4, res.getJavadocLines());
		assertEquals(2, res.getJavadocBlocks());
		assertFalse(res.hasJavadoc());
		
		Resource classResource = res.getFirstChild().getFirstChild().getFirstChild();
		assertTrue(classResource.hasJavadoc());
	}
}
