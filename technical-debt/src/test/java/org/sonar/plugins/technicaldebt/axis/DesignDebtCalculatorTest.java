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

public class DesignDebtCalculatorTest {
  private DecoratorContext context;
  private DesignDebtCalculator calculator;

  @Before
  public void setUp() throws Exception {
    Settings settings = new Settings(new PropertyDefinitions(TechnicalDebtPlugin.class));
    calculator = new DesignDebtCalculator(settings);
    context = mock(DecoratorContext.class);
  }

  @Test
  public void testCalculateWhenNoComment() {
    when(context.getMeasure(CoreMetrics.PACKAGE_TANGLES)).
        thenReturn(null);
    when(context.getMeasure(CoreMetrics.PACKAGE_EDGES_WEIGHT)).
        thenReturn(null);
    assertEquals(0d, calculator.calculateAbsoluteDebt(context), 0);
    assertEquals(0d, calculator.calculateTotalPossibleDebt(context), 0);

    when(context.getMeasure(CoreMetrics.PACKAGE_TANGLES)).
        thenReturn(new Measure(CoreMetrics.PACKAGE_TANGLES, 0.0));
    assertEquals(0d, calculator.calculateAbsoluteDebt(context), 0);

    when(context.getMeasure(CoreMetrics.PACKAGE_EDGES_WEIGHT)).
        thenReturn(new Measure(CoreMetrics.PACKAGE_EDGES_WEIGHT, 0.0));
    assertEquals(0d, calculator.calculateTotalPossibleDebt(context), 0);
  }

  @Test
  public void testCalculateDebt() {
    when(context.getMeasure(CoreMetrics.PACKAGE_TANGLES)).
        thenReturn(new Measure(CoreMetrics.PACKAGE_TANGLES, 16.0));
    assertEquals(8, calculator.calculateAbsoluteDebt(context), 0);

    when(context.getMeasure(CoreMetrics.PACKAGE_EDGES_WEIGHT)).
        thenReturn(new Measure(CoreMetrics.PACKAGE_EDGES_WEIGHT, 100.0));
    assertEquals(25, calculator.calculateTotalPossibleDebt(context), 0);
  }

  @Test
  public void testDependsOn() {
    assertThat(calculator.dependsOn().size(), is(2));
  }
}
