package org.codehaus.sonarncss.sensors;

import org.codehaus.sonarncss.JavaNcss;
import static org.codehaus.sonarncss.JavaNcssUtils.getFile;
import org.codehaus.sonarncss.entities.Resource;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class JavadocSensorTest {

  @Test
  public void analyseJavaDocCounter() {
    Resource res = JavaNcss.analyze(getFile("/metrics/javadoc/ClassWithComments.java"));
    assertEquals(4, res.measures.getJavadocLines());
    assertEquals(3, res.measures.getNonJavadocLines());
    assertEquals(7, res.measures.getCommentLines());
    assertEquals(30, res.measures.getLoc());
    assertEquals(0.23, res.measures.getPercentOfCommentLines(), 0.01);
    assertEquals(1, res.measures.getPercentOfClassesWithJavadoc(), 0);
    assertEquals(0.33, res.measures.getPercentOfMethodsWithJavadoc(), 0.01);
  }
}
