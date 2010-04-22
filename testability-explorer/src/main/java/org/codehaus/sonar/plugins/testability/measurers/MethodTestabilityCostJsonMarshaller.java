/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
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

import static org.codehaus.sonar.plugins.testability.client.model.SerializationConstants.CYCLOMATIC;
import static org.codehaus.sonar.plugins.testability.client.model.SerializationConstants.GLOBAL;
import static org.codehaus.sonar.plugins.testability.client.model.SerializationConstants.LOD;
import static org.codehaus.sonar.plugins.testability.client.model.SerializationConstants.OVERALL;

import org.codehaus.sonar.plugins.testability.client.model.HasCostData;
import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDetail;
/**
 * Marshalls a org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDetail
 * to a Json Member. For instance: <pre>{"cyclomatic":12,"global":400,"lod":10,"overall":400}</pre>
 * 
 * @author cedric.lamalle
 *
 */
public class MethodTestabilityCostJsonMarshaller extends AbstractJsonMarshaller<MethodTestabilityCostDetail> {
  
  private static final int JSON_OBJECT_SIZE = 50;
  
  public String marshall(MethodTestabilityCostDetail detail) {
    StringBuilder stringBuilder = new StringBuilder(JSON_OBJECT_SIZE);
    stringBuilder.append("{");
    writeMemberAndComma(stringBuilder, CYCLOMATIC, detail.getCyclomaticComplexity());
    writeMemberAndComma(stringBuilder, GLOBAL, detail.getGlobal());
    writeMemberAndComma(stringBuilder, LOD, detail.getLawOfDemeter());
    writeMember(stringBuilder, OVERALL, detail.getOverall());
    stringBuilder.append("}");
    return stringBuilder.toString();
  }

  public MethodTestabilityCostDetail unMarshall(String marshalledDetail) {
    // TODO Auto-generated method stub
    return null;
  }

}
