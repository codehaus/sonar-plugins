package org.codehaus.sonarncss.sensors;

import org.codehaus.sonarncss.JavaNcss;
import static org.codehaus.sonarncss.JavaNcssUtils.getFile;
import org.codehaus.sonarncss.entities.Resource;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class NclocSensorTest {

  @Test
  public void analyseTestNcloc() {
    Resource res = JavaNcss.analyze(getFile("/metrics/ncloc/TestNcloc.java"));
    assertEquals(39, res.measures.getLoc());
    assertEquals(9, res.measures.getBlankLines());
    assertEquals(2, res.measures.getCommentLines());
    assertEquals(28, res.measures.getNcloc());
  }
}
