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

package org.sonar.plugins.technicaldebt.axis;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.plugins.technicaldebt.TechnicalDebtPlugin;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ViolationsDebtCalculatorTest {
  private DecoratorContext context;
  private ViolationsDebtCalculator calculator;

  @Before
  public void setUp() throws Exception {
    Settings settings = new Settings(new PropertyDefinitions(TechnicalDebtPlugin.class));
    calculator = new ViolationsDebtCalculator(settings);
    context = mock(DecoratorContext.class);
  }

  @Test
  public void testCalculateAbsoluteDebt() {
    when(context.getMeasure(CoreMetrics.VIOLATIONS)).
        thenReturn(null);
    when(context.getMeasure(CoreMetrics.INFO_VIOLATIONS)).
        thenReturn(null);
    assertEquals(0d, calculator.calculateAbsoluteDebt(context), 0);

    when(context.getMeasure(CoreMetrics.VIOLATIONS)).
        thenReturn(new Measure(CoreMetrics.VIOLATIONS, 160.0));
    when(context.getMeasure(CoreMetrics.INFO_VIOLATIONS)).
        thenReturn(null);
    assertEquals(2d, calculator.calculateAbsoluteDebt(context), 0);

    when(context.getMeasure(CoreMetrics.VIOLATIONS)).
        thenReturn(new Measure(CoreMetrics.VIOLATIONS, 160.0));
    when(context.getMeasure(CoreMetrics.INFO_VIOLATIONS)).
        thenReturn(new Measure(CoreMetrics.VIOLATIONS, 40.0));
    assertEquals(1.5d, calculator.calculateAbsoluteDebt(context), 0);
  }

  @Test
  public void testCalculateTotalDebtNoLines() {

    when(context.getMeasure(CoreMetrics.LINES)).
        thenReturn(null);
    assertEquals(0d, calculator.calculateTotalPossibleDebt(context), 0);
  }

  @Test
  public void testCalculateTotalDebtNoWeightedViolations() {
    when(context.getMeasure(CoreMetrics.WEIGHTED_VIOLATIONS)).
        thenReturn(null);
    when(context.getMeasure(CoreMetrics.VIOLATIONS)).
        thenReturn(new Measure(CoreMetrics.VIOLATIONS, 300.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
        thenReturn(new Measure(CoreMetrics.NCLOC, 300.0));
    assertEquals(1.25d, calculator.calculateTotalPossibleDebt(context), 0);
  }

  @Test
  public void testCalculateTotalDebtNoViolations() {
    when(context.getMeasure(CoreMetrics.VIOLATIONS)).
        thenReturn(null);
    when(context.getMeasure(CoreMetrics.WEIGHTED_VIOLATIONS)).
        thenReturn(new Measure(CoreMetrics.WEIGHTED_VIOLATIONS, 300.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
        thenReturn(new Measure(CoreMetrics.NCLOC, 300.0));
    assertEquals(1.25d, calculator.calculateTotalPossibleDebt(context), 0);
  }

  @Test
  public void testCalculateTotalDebt() {
    when(context.getMeasure(CoreMetrics.VIOLATIONS)).
        thenReturn(new Measure(CoreMetrics.VIOLATIONS, 40.0));
    when(context.getMeasure(CoreMetrics.WEIGHTED_VIOLATIONS)).
        thenReturn(new Measure(CoreMetrics.WEIGHTED_VIOLATIONS, 130.0));
    when(context.getMeasure(CoreMetrics.NCLOC)).
        thenReturn(new Measure(CoreMetrics.NCLOC, 600.0));
    assertEquals(2.3d, calculator.calculateTotalPossibleDebt(context), 0.01);
  }

  @Test
  public void testDependsOn() {
    assertThat(calculator.dependsOn().size(), is(4));
  }
}
