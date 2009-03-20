package org.codehaus.javancss;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class JavaNcssTest {

	@Test
	public void analyseTest102() {
		JavaNcss javaNcss = new JavaNcss(new File("src/test/resources"));
		Resource project = javaNcss.analyseSources();
		assertEquals(3, project.getFiles());
		assertEquals(5, project.getClasses());
	}

}
