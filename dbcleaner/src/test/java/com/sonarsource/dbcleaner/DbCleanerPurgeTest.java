/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
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
