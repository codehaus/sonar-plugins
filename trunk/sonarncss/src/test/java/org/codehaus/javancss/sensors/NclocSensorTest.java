package org.codehaus.javancss.sensors;

import static org.codehaus.javancss.JavaNcssUtils.getFile;
import static org.junit.Assert.assertEquals;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.entities.Resource;
import org.junit.Test;

public class NclocSensorTest {

	@Test
	public void analyseTestNcloc() {
		Resource res = JavaNcss.analyze(getFile("/metrics/ncloc/TestNcloc.java"));
		assertEquals(39, res.measures.getLoc());
		assertEquals(9, res.measures.getBlankLines());
		assertEquals(2, res.measures.getCommentLines());
		assertEquals(28, res.measures.getNcloc());
	}
}
