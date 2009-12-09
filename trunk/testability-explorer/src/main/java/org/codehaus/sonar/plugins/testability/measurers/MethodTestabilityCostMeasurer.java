package org.codehaus.sonar.plugins.testability.measurers;

import static org.codehaus.sonar.plugins.testability.TestabilityMetrics.METHOD_DETAILS_COST;

import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostData;
import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDetail;
import org.codehaus.sonar.plugins.testability.client.model.ViolationCostDetail;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureBuilder;

public class MethodTestabilityCostMeasurer implements MeasureBuilder { 
  
  private MethodTestabilityCostData data = new MethodTestabilityCostData();


  public MethodTestabilityCostData getData() {
    if (this.data == null) {
      this.data = new MethodTestabilityCostData();
    }
    return this.data;
  }
  
  public Measure build() {
    return new Measure(METHOD_DETAILS_COST, new CostDataMarshaller().marshall(getData()));
  }

  public void addViolationCost(int line, ViolationCostDetail violationCost) {
    getData().addViolationCost(line, violationCost);
  } 

  public void addMethodCost(int line, MethodTestabilityCostDetail methodCostDetail) {
    getData().addMethodCost(line, methodCostDetail);
  }

}
