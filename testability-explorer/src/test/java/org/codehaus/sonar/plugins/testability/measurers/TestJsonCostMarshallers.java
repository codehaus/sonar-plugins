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

import junit.framework.Assert;

import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDetail;
import org.codehaus.sonar.plugins.testability.client.model.ViolationCostDetail;
import org.junit.Test;

public class TestJsonCostMarshallers {
  @Test
  public void testMethodTestabilityCostJsonMarshaller() {
    MethodTestabilityCostDetail methodCostDetail = new MethodTestabilityCostDetail(1, 2, 3, 4);
    MethodTestabilityCostJsonMarshaller marshaller = new MethodTestabilityCostJsonMarshaller();
    Assert.assertEquals("MethodTestabilityCostDetail as Json Member", "{\"cyclomatic\":1,\"global\":2,\"lod\":3,\"overall\":4}", marshaller
        .marshall(methodCostDetail));
  }
  @Test
  public void testViolationCostJsonMarshaller() {
    ViolationCostDetail violationCostDetail = new ViolationCostDetail(1, 2, 3, 4, "reason");
    ViolationCostDetailJsonMarshaller marshaller = new ViolationCostDetailJsonMarshaller();
    Assert.assertEquals("MethodTestabilityCostDetail as Json Member","{\"cyclomatic\":1,\"global\":2,\"lod\":3,\"overall\":4,\"reason\":\"reason\"}",marshaller.marshall(violationCostDetail));
  }
}
