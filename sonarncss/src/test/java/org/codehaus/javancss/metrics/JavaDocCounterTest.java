package org.codehaus.javancss.metrics;

import static org.junit.Assert.*;

import java.io.File;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.Resource;
import org.junit.Test;

public class JavaDocCounterTest {

	@Test
	public void analyseJavaDocCounter() {
		JavaNcss javaNcss = new JavaNcss(new File("target/test-classes/JavaDocCounter.java"));
		Resource res = javaNcss.analyseSources();
		assertEquals(4, res.getJavadocLines());
		assertEquals(2, res.getJavadocBlocks());
		assertFalse(res.hasJavadoc());
		
		Resource classResource = res.getFirstChild().getFirstChild().getFirstChild();
		assertTrue(classResource.hasJavadoc());
	}
}
