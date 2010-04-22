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
