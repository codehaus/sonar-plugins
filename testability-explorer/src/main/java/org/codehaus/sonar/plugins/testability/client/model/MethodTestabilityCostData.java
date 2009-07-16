package org.codehaus.sonar.plugins.testability.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodTestabilityCostData {
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
}