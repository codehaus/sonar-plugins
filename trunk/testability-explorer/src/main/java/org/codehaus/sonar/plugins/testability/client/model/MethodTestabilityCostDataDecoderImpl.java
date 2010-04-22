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

import static org.codehaus.sonar.plugins.testability.client.model.SerializationConstants.CYCLOMATIC;
import static org.codehaus.sonar.plugins.testability.client.model.SerializationConstants.GLOBAL;
import static org.codehaus.sonar.plugins.testability.client.model.SerializationConstants.LOD;
import static org.codehaus.sonar.plugins.testability.client.model.SerializationConstants.OVERALL;
import static org.codehaus.sonar.plugins.testability.client.model.SerializationConstants.REASON;

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

  public void parseViolationCosts(JSONObject object, MethodTestabilityCostData methodTestabilityCostData) {
    Set<String> keySet = object.keySet();
    for (String key : keySet) {
      JSONArray violations = object.get(key).isArray();
      for (int i = 0; i < violations.size(); i++) {
        methodTestabilityCostData.addViolationCost(Integer.valueOf(key), createViolationCost(violations.get(i).isObject()));
      }
    }
  }

  public ViolationCostDetail createViolationCost(JSONObject object) {
    return new ViolationCostDetail(getCyclomatic(object), getGlobal(object), getLod(object), getOverall(object), getReason(object));
  }

  private String getReason(JSONObject object) {
    JSONString string = object.get(REASON).isString();
    return string.stringValue();
  }

  public void parseMethodCosts(JSONObject object, MethodTestabilityCostData methodTestabilityCostData) {
    Set<String> keySet = object.keySet();
    for (String key : keySet) {
      methodTestabilityCostData.addMethodCost(Integer.valueOf(key), createMethodCost(object.get(key).isObject()));
    }
  }

  public MethodTestabilityCostDetail createMethodCost(JSONObject object) {
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
