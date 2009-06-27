package org.codehaus.sonar.plugins.testability.measurers;

import org.junit.Assert;
import org.junit.Test;

public class TestCostMarshaller {
  @Test
  public void unMarshalledMethodDetailShouldBeEqualToOriginal() {
    MethodTestabilityCostMarshaller marshaller = new MethodTestabilityCostMarshaller();
    MethodCostDetail methodCostDetail = new MethodCostDetail();
    String marshalledMethodCostDetail = marshaller.marshall(methodCostDetail);
    MethodCostDetail unMarshalledMethodCostDetail = marshaller.unMarshall(marshalledMethodCostDetail);
    Assert.assertEquals("Unmarshalled detail should be equal to original detail", methodCostDetail, unMarshalledMethodCostDetail);
  }
  
}
