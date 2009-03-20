package org.codehaus.javancss.metrics;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.codehaus.javancss.JavaNcss;
import org.codehaus.javancss.Resource;
import org.junit.Test;

public class CommentCounterTest {

	@Test
	public void analyseTest002() {
		Resource res = JavaNcss.analyze(new File("target/test-classes/Test002.java"));
		assertEquals(1, res.getCommentLines());
	}

	@Test
	public void analyseTest001() {
		Resource res = JavaNcss.analyze(new File("target/test-classes/Test001.java"));
		assertEquals(4, res.getCommentLines());
	}
}
