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

package org.codehaus.sonar.plugins.testability.client.model;

import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

public class MethodTestabilityCostDataDecoderGwtTest extends GWTTestCase {

  public void testDecodeMethodTestabilityCostDetail() {
    MethodTestabilityCostDataDecoderImpl decoder = new MethodTestabilityCostDataDecoderImpl();
    JSONValue jsonValue = JSONParser.parse("{\"cyclomatic\":1,\"global\":2,\"lod\":3,\"overall\":4}");
    HasCostData methodCost = decoder.createMethodCost(jsonValue.isObject());
    assertEquals("MethodTestabilityCostDetail - Cyclomatic", 1, methodCost.getCyclomaticComplexity());
    assertEquals("MethodTestabilityCostDetail - Global", 2, methodCost.getGlobal());
    assertEquals("MethodTestabilityCostDetail - Law of Demeter", 3, methodCost.getLawOfDemeter());
    assertEquals("MethodTestabilityCostDetail - Overall", 4, methodCost.getOverall());
  }
  
  public void testDecodeViolationCostDetail() {
    MethodTestabilityCostDataDecoderImpl decoder = new MethodTestabilityCostDataDecoderImpl();
    JSONValue jsonValue = JSONParser.parse("{\"cyclomatic\":1,\"global\":2,\"lod\":3,\"overall\":4,\"reason\":\"reason\"}");
    ViolationCostDetail violationCost = decoder.createViolationCost(jsonValue.isObject());
    assertEquals("MethodTestabilityCostDetail - Cyclomatic", 1, violationCost.getCyclomaticComplexity());
    assertEquals("MethodTestabilityCostDetail - Global", 2, violationCost.getGlobal());
    assertEquals("MethodTestabilityCostDetail - Law of Demeter", 3, violationCost.getLawOfDemeter());
    assertEquals("MethodTestabilityCostDetail - Overall", 4, violationCost.getOverall());
    assertEquals("MethodTestabilityCostDetail - Reason", "reason", violationCost.getReason());
  }
  

  @Override
  public String getModuleName() {
    return "org.codehaus.sonar.plugins.testability.GwtTestabilityDetailsViewer";
  }
}
