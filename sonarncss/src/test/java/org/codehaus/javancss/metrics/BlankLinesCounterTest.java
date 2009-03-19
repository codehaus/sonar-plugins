package org.codehaus.javancss.metrics;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.Resource;
import org.junit.Test;

public class BlankLinesCounterTest {

	@Test
	public void calculateLocOnTest025() {
		JavaNcss javaNcss = new JavaNcss(new File("target/test-classes/Test025.java"));
		Resource res = javaNcss.analyseSources();
		assertEquals(5, res.getBlankLines());
	}

	@Test
	public void calculateLocOnTest002() {
		JavaNcss javaNcss = new JavaNcss(new File("target/test-classes/Test002.java"));
		Resource res = javaNcss.analyseSources();
		assertEquals(4, res.getBlankLines());
	}
}
