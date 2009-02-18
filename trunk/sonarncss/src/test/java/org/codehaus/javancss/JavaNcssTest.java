package org.codehaus.javancss;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;


public class JavaNcssTest extends TestCase {
	
	JavaNcss javaNcss;
	
	public void setUp(){
		List<File> files = new ArrayList<File>();
		files.add(new File("target/test-classes/Test113.java"));
		javaNcss = new JavaNcss(files);
	}
	
	public void testAnalyse(){
		Resource project = javaNcss.analyseSources();
		Resource defaultPackage = project.getChildren().iterator().next();
		Resource testClasse = defaultPackage.getChildren().iterator().next();
		assertNotNull(testClasse);
	}

}
