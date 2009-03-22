package org.codehaus.javancss.sensors;

import static org.codehaus.javancss.JavaNcssUtils.getFile;
import static org.junit.Assert.assertEquals;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.entities.Resource;
import org.junit.Test;

public class CommentSensorTest {

	@Test
	public void analyseTest002() {
		Resource res = JavaNcss.analyze(getFile("/metrics/loc/Test002.java"));
		assertEquals(1, res.measures.getNonJavadocLines());
	}

	@Test
	public void analyseTest001() {
		Resource res = JavaNcss.analyze(getFile("/metrics/loc/Test001.java"));
		assertEquals(2, res.measures.getNonJavadocLines());
	}
}
