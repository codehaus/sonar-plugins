/*
 * Sonar Violation Density Plugin
 * Copyright (C) 2011 MACIF
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
package org.sonar.plugins.violationdensity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Resource;

public class ViolationDensityDecoratorTest {

  @Test
  public void testDecoratorBad() {
    testDecorator(1000D, 250D, 25D);
  }
  
  @Test
  public void testDecoratorVeryBad() {
    testDecorator(1000D, 1500D, 150D);
  }

  @Test
  public void testDecoratorPerfect() {
    testDecorator(1000D, 0D, 0D);
  }

  private void testDecorator(Double ncld, Double wviold, Double expectedResult) {
    final Resource<?> res = mock(Resource.class);
    
    final Measure ncl = mock(Measure.class);
    when(ncl.getValue()).thenReturn(ncld);

    final Measure wviol = mock(Measure.class);
    when(wviol.getValue()).thenReturn(wviold);

    
    final DecoratorContext context = mock(DecoratorContext.class);
    when(context.getMeasure(ViolationDensityMetrics.VIOLATION_DENSITY)).thenReturn(null);
    when(context.getMeasure(CoreMetrics.NCLOC)).thenReturn(ncl);
    when(context.getMeasure(CoreMetrics.WEIGHTED_VIOLATIONS)).thenReturn(wviol);
    
    
    final ViolationDensityDecorator decorator = new ViolationDensityDecorator();
    
    decorator.decorate(res, context);
    
    verify(context).saveMeasure(ViolationDensityMetrics.VIOLATION_DENSITY, expectedResult);
    
  }
  
  
}
