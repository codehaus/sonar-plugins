/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.dbcleaner;

import java.util.Date;
import java.util.GregorianCalendar;

import org.sonar.api.database.model.Snapshot;

public class Utils {
  
  public static Snapshot createSnapshot(int id, String version) {
    Snapshot snapshot = new Snapshot();
    snapshot.setId(id);
    snapshot.setVersion(version);
    return snapshot;
  }

  public static  Snapshot createSnapshot(int id, Date createdAt) {
    Snapshot snapshot = new Snapshot();
    snapshot.setId(id);
    snapshot.setCreatedAt(createdAt);
    return snapshot;
  }

  public static  Date day(int delta) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.add(GregorianCalendar.DAY_OF_YEAR, delta);
    return calendar.getTime();
  }

  public static  Date week(int delta, int dayOfWeek) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.add(GregorianCalendar.WEEK_OF_YEAR, delta);
    calendar.set(GregorianCalendar.DAY_OF_WEEK, dayOfWeek);
    return calendar.getTime();
  }

}
