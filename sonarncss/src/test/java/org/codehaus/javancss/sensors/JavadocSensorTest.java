package org.codehaus.javancss.sensors;

import static org.codehaus.javancss.JavaNcssUtils.getFile;
import static org.junit.Assert.assertEquals;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.entities.Resource;
import org.junit.Test;

public class JavadocSensorTest {

	@Test
	public void analyseJavaDocCounter() {
		Resource res = JavaNcss.analyze(getFile("/metrics/javadoc/ClassWithComments.java"));
		assertEquals(4, res.measures.getJavadocLines());
		assertEquals(3, res.measures.getNonJavadocLines());
		assertEquals(7, res.measures.getCommentLines());
		assertEquals(30, res.measures.getLoc());
		assertEquals(0.23, res.measures.getPercentOfCommentLines(), 0.01);
		assertEquals(1, res.measures.getPercentOfClassesWithJavadoc(), 0);
		assertEquals(0.33, res.measures.getPercentOfMethodsWithJavadoc(), 0.01);
	}
}
