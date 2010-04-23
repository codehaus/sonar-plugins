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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostData;
import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDetail;
import org.codehaus.sonar.plugins.testability.client.model.ViolationCostDetail;

public class CostDataMarshaller extends AbstractJsonMarshaller<MethodTestabilityCostData> {

  private MethodTestabilityCostJsonMarshaller methodTestabilityCostMarshaller;
  private ViolationCostDetailJsonMarshaller violationCostMarshaller;

  public ViolationCostDetailJsonMarshaller getViolationCostMarshaller() {
    if (this.violationCostMarshaller == null) {
      this.violationCostMarshaller = new ViolationCostDetailJsonMarshaller();
    }
    return this.violationCostMarshaller;
  }

  public void setViolationCostMarshaller(ViolationCostDetailJsonMarshaller violationCostMarshaller) {
    this.violationCostMarshaller = violationCostMarshaller;
  }

  public MethodTestabilityCostJsonMarshaller getMethodTestabilityCostMarshaller() {
    if (this.methodTestabilityCostMarshaller == null) {
      this.methodTestabilityCostMarshaller = new MethodTestabilityCostJsonMarshaller();
    }
    return this.methodTestabilityCostMarshaller;
  }

  public void setMethodTestabilityCostMarshaller(MethodTestabilityCostJsonMarshaller marshaller) {
    this.methodTestabilityCostMarshaller = marshaller;
  }

  public String marshall(MethodTestabilityCostData detail) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("{");
    serializeMethodCosts(stringBuilder, detail.getMethodCostsByLine());
    stringBuilder.append(",");
    serializeViolationCosts(stringBuilder, detail.getViolationsCostsByLine());
    stringBuilder.append("}");
    return stringBuilder.toString();
  }

  private void serializeViolationCosts(StringBuilder stringBuilder, Map<Integer, List<ViolationCostDetail>> violationsCostsByLine) {
    stringBuilder.append("\"violationCosts\":{");
    Set<Entry<Integer, List<ViolationCostDetail>>> entrySet = violationsCostsByLine.entrySet();
    for (Entry<Integer, List<ViolationCostDetail>> entry : entrySet) {
      quoteString(stringBuilder, entry.getKey().toString());
      stringBuilder.append(":");
      serializeViolationCostDetails(stringBuilder, entry.getValue());
      stringBuilder.append(",");
    }
    deleteLastChar(stringBuilder);
    stringBuilder.append("}");
  }

  private void serializeViolationCostDetails(StringBuilder stringBuilder, List<ViolationCostDetail> violations) {
    stringBuilder.append("[");
    for (ViolationCostDetail violationCostDetail : violations) {
      stringBuilder.append(getViolationCostMarshaller().marshall(violationCostDetail));
      stringBuilder.append(",");
    }
    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    stringBuilder.append("]");
  }

  private void serializeMethodCosts(StringBuilder stringBuilder, Map<Integer, MethodTestabilityCostDetail> methodCostsByLine) {
    Set<Entry<Integer, MethodTestabilityCostDetail>> entrySet = methodCostsByLine.entrySet();
    stringBuilder.append("\"methodCosts\":{");
    for (Entry<Integer, MethodTestabilityCostDetail> entry : entrySet) {
      quoteString(stringBuilder, entry.getKey().toString());
      stringBuilder.append(":");
      stringBuilder.append(getMethodTestabilityCostMarshaller().marshall(entry.getValue()));
      stringBuilder.append(",");
    }
    deleteLastChar(stringBuilder);
    stringBuilder.append("}");
  }

  private void deleteLastChar(StringBuilder stringBuilder) {
    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
  }

  public MethodTestabilityCostData unMarshall(String marshalledDetail) {
    // TODO Auto-generated method stub
    return null;
  }

}
