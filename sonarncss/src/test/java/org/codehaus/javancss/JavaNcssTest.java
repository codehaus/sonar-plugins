package org.codehaus.javancss;

import static org.codehaus.javancss.JavaNcssUtils.getFile;
import static org.junit.Assert.assertEquals;

import org.codehaus.javancss.entities.Resource;
import org.junit.Test;

public class JavaNcssTest {

	@Test
	public void analyseTest102() {
		Resource project = JavaNcss.analyze(getFile("/"));

		assertEquals(9, project.measures.getFiles());
		assertEquals(11, project.measures.getClasses());
	}

}
