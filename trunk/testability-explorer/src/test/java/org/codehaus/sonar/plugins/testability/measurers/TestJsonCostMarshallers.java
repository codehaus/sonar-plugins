package org.codehaus.sonar.plugins.testability.measurers;

import junit.framework.Assert;

import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDetail;
import org.codehaus.sonar.plugins.testability.client.model.ViolationCostDetail;
import org.junit.Test;

public class TestJsonCostMarshallers {
  @Test
  public void testMethodTestabilityCostJsonMarshaller() {
    MethodTestabilityCostDetail methodCostDetail = new MethodTestabilityCostDetail(1, 2, 3, 4);
    MethodTestabilityCostJsonMarshaller marshaller = new MethodTestabilityCostJsonMarshaller();
    Assert.assertEquals("MethodTestabilityCostDetail as Json Member", "{\"cyclomatic\":1,\"global\":2,\"lod\":3,\"overall\":4}", marshaller
        .marshall(methodCostDetail));
  }
  @Test
  public void testViolationCostJsonMarshaller() {
    ViolationCostDetail violationCostDetail = new ViolationCostDetail(1, 2, 3, 4, "reason");
    ViolationCostDetailJsonMarshaller marshaller = new ViolationCostDetailJsonMarshaller();
    Assert.assertEquals("MethodTestabilityCostDetail as Json Member","{\"cyclomatic\":1,\"global\":2,\"lod\":3,\"overall\":4,\"reason\":\"reason\"}",marshaller.marshall(violationCostDetail));
  }
}
