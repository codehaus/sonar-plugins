package org.codehaus.javancss;

import static org.codehaus.javancss.JavaNcssUtils.getFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.codehaus.javancss.entities.JavaType;
import org.codehaus.javancss.entities.Resource;
import org.junit.Ignore;
import org.junit.Test;

public class JavaNcssTest {

	@Test
	public void testAnalyseCommonsCollections321() {
		Resource prj = JavaNcss.analyze(getFile("/commons-collections-3.2.1-src"));

		assertEquals(12, prj.measures.getPackages());
		assertEquals(273, prj.measures.getFiles());
		assertEquals(412, prj.measures.getClasses());
		assertEquals(3863, prj.measures.getMethods());

		assertEquals(63852, prj.measures.getLoc());
		assertEquals(40201, prj.measures.getNcloc());
		assertEquals(6426, prj.measures.getBlankLines());
		assertEquals(17303, prj.measures.getStatements());
		assertEquals(6842, prj.measures.getComplexity());
		assertEquals(2977, prj.measures.getBranches());

		assertEquals(25.06, prj.measures.getAvgFileCmp(), 0.01);
		assertEquals(16.60, prj.measures.getAvgClassCmp(), 0.01);
		assertEquals(1.77, prj.measures.getAvgMethodCmp(), 0.01);

		assertEquals(63.38, prj.measures.getAvgFileStmts(), 0.01);
		assertEquals(41.99, prj.measures.getAvgClassStmts(), 0.01);
		assertEquals(4.47, prj.measures.getAvgMethodStmts(), 0.01);

		assertEquals(17225, prj.measures.getCommentLines());
		assertEquals(15808, prj.measures.getJavadocLines());
		assertEquals(0.26, prj.measures.getPercentOfCommentLines(), 0.01);
		assertEquals(0.91, prj.measures.getPercentOfClassesWithJavadoc(), 0.01);
		assertEquals(0.64, prj.measures.getPercentOfMethodsWithJavadoc(), 0.01);

		Resource listPackage = prj.find("org.apache.commons.collections.list", JavaType.PACKAGE);
		assertEquals(1403, listPackage.measures.getStatements());
	}

	@Test
	public void testAnalyseWrongFile() {
		Resource prj = JavaNcss.analyze("/fanthomDirectory");
		assertNotNull(prj);
	}

	@Test(expected = IllegalStateException.class)
	public void testAnalyseNullFil() {
		JavaNcss.analyze((File) null);
	}

	@Test
	@Ignore 
	public void testNotUTF8Character() {
		Resource prj = JavaNcss.analyze(getFile("/encoding/NotUTF8Characters.java"));
		assertEquals(3, prj.measures.getMethods());
	}
	
	@Test
	public void testInterfaceWithAnnotations() {
		Resource prj = JavaNcss.analyze(getFile("/annotations/InterfaceWithAnnotation.java"));
		assertEquals(12, prj.measures.getLoc());
		assertEquals(7, prj.measures.getNcloc());
		assertEquals(4, prj.measures.getStatements());
		assertEquals(2, prj.measures.getMethods());
		assertEquals(2, prj.measures.getComplexity());
	}
}
