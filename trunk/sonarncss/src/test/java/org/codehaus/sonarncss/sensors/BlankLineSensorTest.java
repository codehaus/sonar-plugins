package org.codehaus.sonarncss.sensors;

import org.codehaus.sonarncss.JavaNcss;
import static org.codehaus.sonarncss.JavaNcssUtils.getFile;
import org.codehaus.sonarncss.entities.Resource;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BlankLineSensorTest {

  @Test
  public void analyseTest002() {
    Resource res = JavaNcss.analyze(getFile("/metrics/loc/Test002.java"));
    assertEquals(5, res.measures.getBlankLines());
  }

  @Test
  public void analyseTest001() {
    Resource res = JavaNcss.analyze(getFile("/metrics/loc/Test001.java"));
    assertEquals(3, res.measures.getBlankLines());
  }
}
