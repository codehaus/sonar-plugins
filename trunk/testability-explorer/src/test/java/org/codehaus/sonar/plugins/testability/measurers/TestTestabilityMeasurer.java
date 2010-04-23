/*
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

import junit.framework.Assert;

import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDetail;
import org.codehaus.sonar.plugins.testability.client.model.ViolationCostDetail;
import org.junit.Test;

public class TestTestabilityMeasurer {
  @Test
  public void testMethodTestabilityCostMeasurer() {
    MethodTestabilityCostDetail methodCostDetail = new MethodTestabilityCostDetail(1, 2, 3, 4);
    ViolationCostDetail violationCostDetail = new ViolationCostDetail(1, 2, 3, 4, "reason");
    ViolationCostDetail violationCostDetail2 = new ViolationCostDetail(1, 2, 3, 4, "reason2");
    MethodTestabilityCostMeasurer measurer = new MethodTestabilityCostMeasurer();
    measurer.addMethodCost(21, methodCostDetail);
    measurer.addViolationCost(22, violationCostDetail);
    measurer.addViolationCost(22, violationCostDetail2);
    String expected = "{\"methodCosts\":{\"21\":" + "{\"cyclomatic\":1,\"global\":2,\"lod\":3,\"overall\":4}"
        + "},\"violationCosts\":{\"22\":[" + "{\"cyclomatic\":1,\"global\":2,\"lod\":3,\"overall\":4,\"reason\":\"reason\"},"
        + "{\"cyclomatic\":1,\"global\":2,\"lod\":3,\"overall\":4,\"reason\":\"reason2\"}" + "]}}";
    Assert.assertEquals("", expected, measurer.build().getData());
  }
}
