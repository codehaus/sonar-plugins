/*
 * Sonar Cutoff Plugin
 * Copyright (C) 2010 SonarSource
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

package org.sonar.plugins.cutoff;

import org.junit.Test;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.SonarException;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.number.OrderingComparisons.greaterThan;
import static org.hamcrest.number.OrderingComparisons.lessThan;
import static org.junit.Assert.assertThat;

public class CutoffFilterTest {
  @Test
  public void shouldParseDate() {
    Settings settings = new Settings();
    settings.setProperty(CutoffConstants.DATE_PROPERTY, "2009-05-18");

    CutoffFilter filter = new CutoffFilter(settings);
    filter.start();

    assertThat(filter.getCutoffDate().getDate(), is(18));
  }

  @Test(expected = SonarException.class)
  public void shouldFailIfDateIsBadlyFormed() {
    Settings settings = new Settings();
    settings.setProperty(CutoffConstants.DATE_PROPERTY, "2009/18/05");

    new CutoffFilter(settings).start();
  }

  @Test
  public void shouldUsePeriodIfDateIsNotSet() {
    Settings settings = new Settings();
    settings.setProperty(CutoffConstants.PERIOD_IN_DAYS_PROPERTY, "10");

    CutoffFilter filter = new CutoffFilter(settings);
    filter.start();

    assertThat(filter.getCutoffDate().getTime(), greaterThan(System.currentTimeMillis() - 11 * 24 * 60 * 60 * 1000));
    assertThat(filter.getCutoffDate().getTime(), lessThan(System.currentTimeMillis() - 9 * 24 * 60 * 60 * 1000));
  }

  @Test
  public void shouldBeInactiveIfNoCutoffDate() {
    Settings settings = new Settings();
    CutoffFilter filter = new CutoffFilter(settings);
    filter.start();
    assertThat(filter.getCutoffDate(), nullValue());
    assertThat(filter.accept(new File("pom.xml")), is(true));
  }
}
