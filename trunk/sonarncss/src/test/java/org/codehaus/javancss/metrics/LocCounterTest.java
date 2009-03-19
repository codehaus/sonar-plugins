package org.codehaus.javancss.metrics;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.Resource;
import org.junit.Test;

public class LocCounterTest {

	@Test
	public void calculateLocOnTest001() {
		JavaNcss javaNcss = new JavaNcss(new File("target/test-classes/Test001.java"));
		Resource res = javaNcss.analyseSources();
		assertEquals(540, res.getLoc());
	}
	
	@Test
	public void calculateLocOnTest025() {
		JavaNcss javaNcss = new JavaNcss(new File("target/test-classes/Test025.java"));
		Resource res = javaNcss.analyseSources();
		assertEquals(19, res.getLoc());
	}
}
