/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
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

package org.sonar.plugins.sigmm.axis;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.plugins.sigmm.MMRank;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static junit.framework.Assert.assertNull;

public class AdvancedAxisTest {

  @Test
  public void testGetRank() {
    Number[] bLimits = {50, 20, 10, 0};
    assertEquals(new AdvancedAxis(bLimits, getContext("50=0;20=0;10=0;0=0", 100), CoreMetrics.ACCESSORS).getRank(), MMRank.PLUSPLUS);
    assertEquals(new AdvancedAxis(bLimits, getContext("50=0;20=0;10=25;0=0", 100), CoreMetrics.ACCESSORS).getRank(), MMRank.PLUSPLUS);
    assertEquals(new AdvancedAxis(bLimits, getContext("50=0;20=1;10=25;0=0", 100), CoreMetrics.ACCESSORS).getRank(), MMRank.PLUS);
    assertEquals(new AdvancedAxis(bLimits, getContext("50=0;20=9;10=20;0=0", 100), CoreMetrics.ACCESSORS).getRank(), MMRank.ZERO);
    assertEquals(new AdvancedAxis(bLimits, getContext("50=4;20=0;10=0;0=0", 100), CoreMetrics.ACCESSORS).getRank(), MMRank.MINUS);
    assertEquals(new AdvancedAxis(bLimits, getContext("50=10;20=0;10=0;0=0", 100), CoreMetrics.ACCESSORS).getRank(), MMRank.MINUSMINUS);

    assertNull(new AdvancedAxis(bLimits, getContext("50=10;20=0;10=0;0=0", 0), CoreMetrics.ACCESSORS).getRank());
  }

  private DecoratorContext getContext(String value,double ncloc) {
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getMeasure(CoreMetrics.ACCESSORS)).
      thenReturn(new Measure(CoreMetrics.ACCESSORS, value));
    when(context.getMeasure(CoreMetrics.NCLOC)).
      thenReturn(new Measure(CoreMetrics.NCLOC, ncloc));
    return context;
  }
}
