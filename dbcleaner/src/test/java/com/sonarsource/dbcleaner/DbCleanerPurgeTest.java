/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SonarSource
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
package com.sonarsource.dbcleaner;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DbCleanerPurgeTest {

  DbCleanerPurge purge = new DbCleanerPurge(null, null);

  @Test
  public void getDateShouldReturnCurrentTimeMinusDesiredMonths() {
    Configuration conf = mock(Configuration.class);
    when(conf.getInt("KEY", 2)).thenReturn(2);

    Date date = purge.getDate(conf, "KEY", "2");

    GregorianCalendar calendar = new GregorianCalendar();
    calendar.add(GregorianCalendar.MONTH, -2);
    Date expectedDate = calendar.getTime();

    assertThat(date.getMonth(), is(expectedDate.getMonth()));
  }
}
