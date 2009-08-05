package org.codehaus.sonar.plugins.testability.client.model;

import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

public class MethodTestabilityCostDataDecoderGwtTest extends GWTTestCase {

  public void testDecodeMethodTestabilityCostDetail() {
    MethodTestabilityCostDataDecoderImpl decoder = new MethodTestabilityCostDataDecoderImpl();
    JSONValue jsonValue = JSONParser.parse("{\"cyclomatic\":1,\"global\":2,\"lod\":3,\"overall\":4}");
    MethodTestabilityCostDetail methodCost = decoder.createMethodCost(jsonValue.isObject());
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
