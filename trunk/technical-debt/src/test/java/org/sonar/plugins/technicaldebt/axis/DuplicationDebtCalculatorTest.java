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

package org.sonar.plugins.technicaldebt.axis;

import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.plugins.technicaldebt.TechnicalDebtPlugin;
import static org.mockito.Mockito.mock;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import org.apache.commons.configuration.Configuration;
import static org.hamcrest.Matchers.is;


public class DuplicationDebtCalculatorTest {
  private DecoratorContext context;
  private DuplicationDebtCalculator calculator;

  @Before
  public void setUp() throws Exception {
    Configuration configuration = mock(Configuration.class);
    when(configuration.getString(anyString(), anyString())).
      thenReturn(TechnicalDebtPlugin.TD_COST_DUPLI_BLOCK_DEFAULT);
    calculator = new DuplicationDebtCalculator(configuration);
    context = mock(DecoratorContext.class);
  }

  @Test
  public void testCalculateAbsoluteDebt() {
    when(context.getMeasure(CoreMetrics.DUPLICATED_BLOCKS)).
      thenReturn(null);
    assertEquals(0d, calculator.calculateAbsoluteDebt(context), 0);

    when(context.getMeasure(CoreMetrics.DUPLICATED_BLOCKS)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_BLOCKS, 0.0));
    assertEquals(0d, calculator.calculateAbsoluteDebt(context), 0);

    when(context.getMeasure(CoreMetrics.DUPLICATED_BLOCKS)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_BLOCKS, 32.0));
    assertEquals(8d, calculator.calculateAbsoluteDebt(context), 0);
  }

  @Test
  public void testCalculateTotalDebt() {
    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES_DENSITY)).
      thenReturn(null);
    assertEquals(0d, calculator.calculateTotalPossibleDebt(context), 0);

    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES_DENSITY)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES_DENSITY, 0.0));
    assertEquals(0d, calculator.calculateTotalPossibleDebt(context), 0);

    when(context.getMeasure(CoreMetrics.DUPLICATED_LINES_DENSITY)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_LINES_DENSITY, 40.0));
    when(context.getMeasure(CoreMetrics.DUPLICATED_BLOCKS)).
      thenReturn(new Measure(CoreMetrics.DUPLICATED_BLOCKS, 32.0));
    assertEquals(20d, calculator.calculateTotalPossibleDebt(context), 0);
  }

  @Test
  public void testDependsOn() {
    assertThat(calculator.dependsOn().size(), is(2));
  }
}
