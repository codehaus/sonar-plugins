package org.codehaus.sonar.plugins.testability.measurers;

import junit.framework.Assert;

import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDetail;
import org.codehaus.sonar.plugins.testability.client.model.ViolationCostDetail;
import org.junit.Test;

public class TestTestabilityMeasurer {
  @Test
  public void testMethodTestabilityCostMeasurer() {
    MethodTestabilityCostDetail methodCostDetail = new MethodTestabilityCostDetail(1, 2, 3, 4);
    ViolationCostDetail violationCostDetail = new ViolationCostDetail(1, 2, 3, 4, "reason");
    ViolationCostDetail violationCostDetail2 = new ViolationCostDetail(1, 2, 3, 4, "reason2");
    MethodTestabilityCostMeasurer measurer = new MethodTestabilityCostMeasurer();
    measurer.addMethodCost(21, methodCostDetail);
    measurer.addViolationCost(22, violationCostDetail);
    measurer.addViolationCost(22, violationCostDetail2);
    String expected = "{\"methodCosts\":{\"21\":" + "{\"cyclomatic\":1,\"global\":2,\"lod\":3,\"overall\":4}"
        + "},\"violationCosts\":{\"22\":[" + "{\"cyclomatic\":1,\"global\":2,\"lod\":3,\"overall\":4,\"reason\":\"reason\"},"
        + "{\"cyclomatic\":1,\"global\":2,\"lod\":3,\"overall\":4,\"reason\":\"reason2\"}" + "]}}";
    Assert.assertEquals("", expected, measurer.build().getData());
  }
}
