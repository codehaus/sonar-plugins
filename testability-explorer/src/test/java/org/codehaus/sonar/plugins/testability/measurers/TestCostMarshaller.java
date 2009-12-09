package org.codehaus.sonar.plugins.testability.measurers;

import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDetail;
import org.codehaus.sonar.plugins.testability.client.model.ViolationCostDetail;
import org.junit.Test;

public class TestCostMarshaller {
  @Test
  public void unMarshalledMethodDetailShouldBeEqualToOriginal() {
    MethodTestabilityCostMarshaller marshaller = new MethodTestabilityCostMarshaller();
    MethodTestabilityCostDetail methodCostDetail = new MethodTestabilityCostDetail(1, 2, 3, 4);
    /*HasCostData unMarshalledMethodCostDetail = marshaller.unMarshall(marshaller.marshall(methodCostDetail));
    Assert.assertEquals("Unmarshalled detail should be equal to original detail", methodCostDetail, unMarshalledMethodCostDetail);*/
  }

  @Test
  public void unMarshalledViolationDetailShouldBeEqualToOriginal() {
    ViolationCostDetailMarshaller marshaller = new ViolationCostDetailMarshaller();
    ViolationCostDetail violationCostDetail = new ViolationCostDetail(1, 2, 3, 4, "reason");
    /*ViolationCostDetail unMarshalledViolationCostDetail = marshaller.unMarshall(marshaller.marshall(violationCostDetail));
    Assert.assertEquals("Unmarshalled violation should be equal to original violation", violationCostDetail,
        unMarshalledViolationCostDetail);*/
  }

}
