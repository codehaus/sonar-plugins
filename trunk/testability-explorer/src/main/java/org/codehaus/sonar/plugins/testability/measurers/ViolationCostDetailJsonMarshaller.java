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

import static org.codehaus.sonar.plugins.testability.client.model.SerializationConstants.*;
import org.codehaus.sonar.plugins.testability.client.model.ViolationCostDetail;

public class ViolationCostDetailJsonMarshaller extends AbstractJsonMarshaller<ViolationCostDetail> {
  
  public String marshall(ViolationCostDetail detail) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("{");
    writeMemberAndComma(stringBuilder, CYCLOMATIC, detail.getCyclomaticComplexity());
    writeMemberAndComma(stringBuilder, GLOBAL, detail.getGlobal());
    writeMemberAndComma(stringBuilder, LOD, detail.getLawOfDemeter());
    writeMemberAndComma(stringBuilder, OVERALL, detail.getOverall());
    writeMember(stringBuilder, REASON, detail.getReason(), true);
    stringBuilder.append("}");
    return stringBuilder.toString();
  }

  public ViolationCostDetail unMarshall(String marshalledDetail) {
    // TODO Auto-generated method stub
    return null;
  }

}
