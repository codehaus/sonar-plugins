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
package org.sonar.plugins.cutoff;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.cutoff.CutoffConstants;
import org.sonar.plugins.cutoff.CutoffFilter;

import java.io.File;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.number.OrderingComparisons.greaterThan;
import static org.hamcrest.number.OrderingComparisons.lessThan;
import static org.junit.Assert.assertThat;

public class CutoffFilterTest {
  @Test
  public void shouldParseDate() {
    Configuration conf = new PropertiesConfiguration();
    conf.setProperty(CutoffConstants.DATE_PROPERTY, "2009-05-18");
    assertThat(new CutoffFilter(conf).getCutoffDate().getDate(), is(18));
  }

  @Test(expected= SonarException.class)
  public void shouldFailIfDateIsBadlyFormed() {
    Configuration conf = new PropertiesConfiguration();
    conf.setProperty(CutoffConstants.DATE_PROPERTY, "2009/18/05");
    new CutoffFilter(conf);
  }

  @Test
  public void shouldUsePeriodIfDateIsNotSet() {
    Configuration conf = new PropertiesConfiguration();
    conf.setProperty(CutoffConstants.PERIOD_IN_DAYS_PROPERTY, "10");
    assertThat(new CutoffFilter(conf).getCutoffDate().getTime(), greaterThan(System.currentTimeMillis() - 11 * 24 * 60 * 60 * 1000));
    assertThat(new CutoffFilter(conf).getCutoffDate().getTime(), lessThan(System.currentTimeMillis() - 9 * 24 * 60 * 60 * 1000));
  }

  @Test
  public void shouldBeInactiveIfNoCutoffDate() {
    Configuration conf = new PropertiesConfiguration();
    CutoffFilter filter = new CutoffFilter(conf);
    assertThat(filter.getCutoffDate(), nullValue());
    assertThat(filter.accept(new File("pom.xml")), is(true));
  }
}
