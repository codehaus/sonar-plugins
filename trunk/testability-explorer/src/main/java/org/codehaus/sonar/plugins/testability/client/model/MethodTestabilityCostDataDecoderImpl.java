package org.codehaus.sonar.plugins.testability.client.model;

import static org.codehaus.sonar.plugins.testability.client.model.SerializationConstants.*;
import java.util.Set;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class MethodTestabilityCostDataDecoderImpl implements MethodTestabilityCostDataDecoder {

  public MethodTestabilityCostData decode(String data) {
    JSONValue jsonValue = JSONParser.parse(data);
    JSONObject rootObject = jsonValue.isObject();
    MethodTestabilityCostData methodTestabilityCostData = new MethodTestabilityCostData();
    parseMethodCosts(rootObject.get("methodCosts").isObject(), methodTestabilityCostData);
    parseViolationCosts(rootObject.get("violationCosts").isObject(), methodTestabilityCostData);
    return methodTestabilityCostData;
  }

  private void parseViolationCosts(JSONObject object, MethodTestabilityCostData methodTestabilityCostData) {
    Set<String> keySet = object.keySet();
    for (String key : keySet) {
      JSONArray violations = object.get(key).isArray();
      for (int i = 0; i < violations.size(); i++) {
        methodTestabilityCostData.addViolationCost(Integer.valueOf(key), createViolationCost(violations.get(i).isObject()));
      }
    }
  }

  private ViolationCostDetail createViolationCost(JSONObject object) {
    return new ViolationCostDetail(getCyclomatic(object), getGlobal(object), getLod(object), getOverall(object), getReason(object));
  }

  private String getReason(JSONObject object) {
    JSONString string = object.get(REASON).isString();
    return string.stringValue();
  }

  private void parseMethodCosts(JSONObject object, MethodTestabilityCostData methodTestabilityCostData) {
    Set<String> keySet = object.keySet();
    for (String key : keySet) {
      methodTestabilityCostData.addMethodCost(Integer.valueOf(key), createMethodCost(object.get(key).isObject()));
    }
  }

  private MethodTestabilityCostDetail createMethodCost(JSONObject object) {
    return new MethodTestabilityCostDetail(getCyclomatic(object), getGlobal(object), getLod(object), getOverall(object));
  }

  private int getOverall(JSONObject object) {
    return getInt(object.get(OVERALL));
  }

  private int getLod(JSONObject object) {
    return getInt(object.get(LOD));
  }

  private int getGlobal(JSONObject object) {
    return getInt(object.get(GLOBAL));
  }

  private int getInt(JSONValue value) {
    double doubleValue = value.isNumber().doubleValue();
    return Double.valueOf(doubleValue).intValue();
  }

  private int getCyclomatic(JSONObject object) {
    return getInt(object.get(CYCLOMATIC));
  }

}
