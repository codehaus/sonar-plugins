package org.codehaus.sonarncss.sensors;

import org.codehaus.sonarncss.JavaNcss;
import static org.codehaus.sonarncss.JavaNcssUtils.getFile;
import org.codehaus.sonarncss.entities.JavaType;
import org.codehaus.sonarncss.entities.Resource;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ComplexitySensorTest {

  @Test
  public void testNoBranches() {
    Resource res = JavaNcss.analyze(getFile("/metrics/branches/NoBranches.java"));
    assertEquals(3, res.measures.getComplexity());
  }

  @Test
  public void testSimpleBranches() {
    Resource res = JavaNcss.analyze(getFile("/metrics/branches/SimpleBranches.java"));
    assertEquals(15, res.measures.getComplexity());
    assertEquals(2.14, res.measures.getAvgMethodCmp(), 0.01);

    Resource simpleSwitch = res.find("simpleSwitch()", JavaType.METHOD);
    assertEquals(3, simpleSwitch.measures.getComplexity());
  }

  @Test
  public void testInstanceAndStaticInitBlocks() {
    Resource res = JavaNcss.analyze(getFile("/metrics/complexity/InstanceAndStaticInitBlocks.java"));
    assertEquals(2, res.measures.getComplexity());
  }
}
