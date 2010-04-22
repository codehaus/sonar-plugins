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

import java.util.HashMap;
import java.util.Map;

import org.codehaus.sonar.plugins.testability.client.model.HasCostData;
import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDetail;
import org.sonar.api.utils.KeyValueFormat;

public class MethodTestabilityCostMarshaller implements CostMarshaller<MethodTestabilityCostDetail> {

  private static final String OVERALL = "overall";
  private static final String LOD = "lod";
  private static final String CYCLOMATIC = "cyclomatic";
  private static final String GLOBAL = "global";

  public String marshall(MethodTestabilityCostDetail detail) {
    HashMap<Object, Object> map = new HashMap<Object, Object>();
    map.put(GLOBAL, detail.getGlobal());
    map.put(CYCLOMATIC, detail.getCyclomaticComplexity());
    map.put(LOD, detail.getLawOfDemeter());
    map.put(OVERALL, detail.getOverall());
    return KeyValueFormat.format(map);
  }

  public MethodTestabilityCostDetail unMarshall(String marshalledDetail) {
    Map<String, String> map = KeyValueFormat.parse(marshalledDetail);
    MethodTestabilityCostDetail method = new MethodTestabilityCostDetail();
    method.setGlobal(Integer.valueOf(map.get(GLOBAL)));
    method.setCyclomaticComplexity(Integer.valueOf(map.get(CYCLOMATIC)));
    method.setLawOfDemeter(Integer.valueOf(map.get(LOD)));
    method.setOverall(Integer.valueOf(map.get(OVERALL)));
    return method;
  }
  
}
