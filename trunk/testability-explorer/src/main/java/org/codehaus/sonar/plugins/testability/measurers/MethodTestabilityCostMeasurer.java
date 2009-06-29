package org.codehaus.sonar.plugins.testability.measurers;

import static org.codehaus.sonar.plugins.testability.TestabilityMetrics.METHOD_DETAILS_COST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sonar.commons.resources.Measure;
import org.sonar.plugins.api.measures.MeasureBuilder;

public class MethodTestabilityCostMeasurer implements MeasureBuilder { 
  
  private Map<Integer, List<ViolationCostDetail>> violationsCostsByLine;
  private Map<Integer, MethodTestabilityCostDetail> methodCostsByLine;
  
  
  public Measure build() {
    return new Measure(METHOD_DETAILS_COST, Double.valueOf(0));
  }
  
  public void addViolationCost(int line, ViolationCostDetail violationCost) {
    List<ViolationCostDetail> lineViolations = getViolationsOfLine(line);
    lineViolations.add(violationCost);
  }
  
  private List<ViolationCostDetail> getViolationsOfLine(int line) {
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

  public Map<Integer, List<ViolationCostDetail>> getViolationsCostsByLine() {
    if (this.violationsCostsByLine == null) {
      this.violationsCostsByLine = new HashMap<Integer, List<ViolationCostDetail>>();
    }
    return this.violationsCostsByLine;
  }


  public void setViolationsCostsByLine(Map<Integer, List<ViolationCostDetail>> violationsCostsByLine) {
    this.violationsCostsByLine = violationsCostsByLine;
  }


  public Map<Integer, MethodTestabilityCostDetail> getMethodCostsByLine() {
    if (this.methodCostsByLine == null) {
      this.methodCostsByLine = new HashMap<Integer, MethodTestabilityCostDetail>();
    }
    return this.methodCostsByLine;
  }


  public void setMethodCostsByLine(Map<Integer, MethodTestabilityCostDetail> methodCostsByLine) {
    this.methodCostsByLine = methodCostsByLine;
  }

}
