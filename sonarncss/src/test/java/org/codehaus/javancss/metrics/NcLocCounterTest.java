package org.codehaus.javancss.metrics;

import static org.codehaus.javancss.JavaNcssUtils.getFile;
import static org.junit.Assert.assertEquals;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.Resource;
import org.junit.Test;

public class NcLocCounterTest {

	@Test
	public void analyseTest002() {
		Resource res = JavaNcss.analyze(getFile("/Test002.java"));
		assertEquals(13, res.getNcloc());
	}
}
