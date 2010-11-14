/*
 * Sonar GreenPepper Plugin
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

package org.sonar.plugins.greenpepper;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.batch.AbstractSumChildrenDecorator;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.Metric;

public class GreenPepperDecorator extends AbstractSumChildrenDecorator {

  @DependedUpon
  public List<Metric> generatesMetrics() {
    return Arrays.asList(GreenPepperMetrics.GREENPEPPER_SKIPPED_TESTS, GreenPepperMetrics.GREENPEPPER_TEST_ERRORS,
        GreenPepperMetrics.GREENPEPPER_TEST_FAILURES, GreenPepperMetrics.GREENPEPPER_TEST_SUCCESS_DENSITY,
        GreenPepperMetrics.GREENPEPPER_TESTS);
  }

  @Override
  protected boolean shouldSaveZeroIfNoChildMeasures() {
    return false;
  }

}
