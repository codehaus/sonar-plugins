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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodTestabilityCostData implements CostDetail {
  private Map<Integer, List<ViolationCostDetail>> violationsCostsByLine;
  private Map<Integer, MethodTestabilityCostDetail> methodCostsByLine;

  public MethodTestabilityCostData() {
  }

  public Map<Integer, List<ViolationCostDetail>> getViolationsCostsByLine() {
    if (this.violationsCostsByLine == null) {
      this.violationsCostsByLine = new HashMap<Integer, List<ViolationCostDetail>>();
    }
    return this.violationsCostsByLine;
  }

  public Map<Integer, MethodTestabilityCostDetail> getMethodCostsByLine() {
    if (this.methodCostsByLine == null) {
      this.methodCostsByLine = new HashMap<Integer, MethodTestabilityCostDetail>();
    }
    return this.methodCostsByLine;
  }
  
  public List<ViolationCostDetail> getViolationsOfLine(int line) {
    List<ViolationCostDetail> lineViolations = getViolationsCostsByLine().get(line);
    if (lineViolations == null) {
      lineViolations = new ArrayList<ViolationCostDetail>();
      getViolationsCostsByLine().put(line, lineViolations);
    }
    return lineViolations;
  }
  
  public void addMethodCost(int line, MethodTestabilityCostDetail methodCostDetail) {
    getMethodCostsByLine().put(line, methodCostDetail);
  }
  
  public void addViolationCost(int line, ViolationCostDetail violationCost) {
    List<ViolationCostDetail> lineViolations = getViolationsOfLine(line);
    lineViolations.add(violationCost);
  }

  public HasCostData getMethodCostOfLine(int lineIndex) {
    return getMethodCostsByLine().get(lineIndex);
  } 
}