package org.codehaus.sonar.plugins.testability.measurers;

import static org.codehaus.sonar.plugins.testability.TestabilityMetrics.METHOD_DETAILS_COST;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sonar.commons.resources.Measure;
import org.sonar.plugins.api.measures.MeasureBuilder;

public class MethodTestabilityCostMeasurer implements MeasureBuilder { 
  
  private Map<Integer, List<VioltationCostDetail>> violationsCostsByLine;
  private Map<Integer, MethodCostDetail> methodCostsByLine;
  
  
  public Measure build() {
    return new Measure(METHOD_DETAILS_COST, Double.valueOf(0));
  }
  
  public void addViolationCost(int line, VioltationCostDetail violationCost) {
    //
  }
  
  public void addMethodCost(int line, MethodCostDetail methodCostDetail) {
    //
  }

  public Map<Integer, List<VioltationCostDetail>> getViolationsCostsByLine() {
    if (this.violationsCostsByLine == null) {
      this.violationsCostsByLine = new HashMap<Integer, List<VioltationCostDetail>>();
    }
    return this.violationsCostsByLine;
  }


  public void setViolationsCostsByLine(Map<Integer, List<VioltationCostDetail>> violationsCostsByLine) {
    this.violationsCostsByLine = violationsCostsByLine;
  }


  public Map<Integer, MethodCostDetail> getMethodCostsByLine() {
    if (this.methodCostsByLine == null) {
      this.methodCostsByLine = new HashMap<Integer, MethodCostDetail>();
    }
    return this.methodCostsByLine;
  }


  public void setMethodCostsByLine(Map<Integer, MethodCostDetail> methodCostsByLine) {
    this.methodCostsByLine = methodCostsByLine;
  }

}
