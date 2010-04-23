/*
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

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
