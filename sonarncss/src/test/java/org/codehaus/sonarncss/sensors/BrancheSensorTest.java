package org.codehaus.sonarncss.sensors;

import org.codehaus.sonarncss.JavaNcss;
import static org.codehaus.sonarncss.JavaNcssUtils.getFile;
import org.codehaus.sonarncss.entities.Resource;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BrancheSensorTest {

  @Test
  public void testNoBranches() {
    Resource res = JavaNcss.analyze(getFile("/metrics/branches/NoBranches.java"));
    assertEquals(0, res.measures.getBranches());
  }

  @Test
  public void testSimpleBranches() {
    Resource res = JavaNcss.analyze(getFile("/metrics/branches/SimpleBranches.java"));
    assertEquals(8, res.measures.getBranches());
  }
}
