package org.codehaus.javancss;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class JavaNcssTest {

	@Test
	public void analyseTest102() {
		JavaNcss javaNcss = new JavaNcss(new File("src/test/resources"));
		Resource project = javaNcss.analyseSources();
		assertEquals(55, project.getFiles());
		assertEquals(83, project.getClasses());
	}

}
