package org.codehaus.javancss.sensors;

import static org.codehaus.javancss.JavaNcssUtils.getFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.entities.Resource;
import org.junit.Test;

public class JavadocSensorTest {

	@Test
	public void analyseJavaDocCounter() {
		Resource res = JavaNcss.analyze(getFile("/JavaDocCounter.java"));
		assertEquals(4, res.measures.getJavadocLines());
		assertEquals(2, res.measures.getJavadocBlocks());
		assertFalse(res.measures.hasJavadoc());

		Resource classResource = res.getFirstChild().getFirstChild().getFirstChild();
		assertTrue(classResource.measures.hasJavadoc());
	}
}
