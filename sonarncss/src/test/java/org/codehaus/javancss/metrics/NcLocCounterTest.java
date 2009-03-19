package org.codehaus.javancss.metrics;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.Resource;
import org.junit.Test;

public class NcLocCounterTest {

	@Test
	public void analyseTest025() {
		JavaNcss javaNcss = new JavaNcss(new File("target/test-classes/Test025.java"));
		Resource res = javaNcss.analyseSources();
		assertEquals(13, res.getNcloc());
	}
}
