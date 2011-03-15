/*
 * Technical Debt Sonar plugin
 * Copyright (C) 2009 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.technicaldebt;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.resources.Project;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class ComplexityDebtDecoratorTest {

  private ComplexityDebtDecorator decorator;

  @Before
  public void setUp() {
    decorator = new ComplexityDebtDecorator(60, 8);
  }

  @Test
  public void dependedUpon() {
    assertThat(decorator.dependedUpon(), is(TechnicalDebtMetrics.TECHNICAL_DEBT_COMPLEXITY));
  }

  @Test
  public void shouldExecuteOnAnyProject() {
    assertThat(decorator.shouldExecuteOnProject(mock(Project.class)), is(true));
  }

  @Test
  public void shouldReadDefaultConfiguration() {
    PropertiesConfiguration conf = new PropertiesConfiguration();
    ComplexityDebtDecorator debtDecorator = new ComplexityDebtDecorator(conf);
    assertThat(debtDecorator.classThreshold, is(60.0));
    assertThat(debtDecorator.methodThreshold, is(8.0));
    assertThat(debtDecorator.classSplitCost, is(TechnicalDebtPlugin.COST_CLASS_COMPLEXITY_DEFVAL));
    assertThat(debtDecorator.methodSplitCost, is(TechnicalDebtPlugin.COST_METHOD_COMPLEXITY_DEFVAL));
  }

  @Test
  public void shouldNotFailIfMissingThreshold() {
    PropertiesConfiguration conf = new PropertiesConfiguration();
    conf.setProperty(TechnicalDebtPlugin.COMPLEXITY_THRESHOLDS, "METHOD=8");
    ComplexityDebtDecorator debtDecorator = new ComplexityDebtDecorator(conf);
    assertThat(debtDecorator.classThreshold, is(Double.MAX_VALUE));
    assertThat(debtDecorator.methodThreshold, is(8.0));
  }

  @Test
  public void shouldNotFailIfEmptyThreshold() {
    PropertiesConfiguration conf = new PropertiesConfiguration();
    conf.setProperty(TechnicalDebtPlugin.COMPLEXITY_THRESHOLDS, "CLASS=;METHOD=");
    ComplexityDebtDecorator debtDecorator = new ComplexityDebtDecorator(conf);
    assertThat(debtDecorator.classThreshold, is(Double.MAX_VALUE));
    assertThat(debtDecorator.methodThreshold, is(Double.MAX_VALUE));
  }
}
