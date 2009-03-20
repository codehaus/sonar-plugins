package org.codehaus.javancss;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JavaNcssTest {

	@Test
	public void analyseTest102() {
		Resource project = JavaNcss.analyze("src/test/resources");

		assertEquals(8, project.getFiles());
		assertEquals(10, project.getClasses());
	}

}
