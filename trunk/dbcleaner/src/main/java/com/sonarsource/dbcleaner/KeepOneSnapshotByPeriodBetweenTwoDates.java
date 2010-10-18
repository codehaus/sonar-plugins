/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.dbcleaner;

import java.util.Date;
import java.util.GregorianCalendar;

import org.sonar.api.database.model.Snapshot;


public class KeepOneSnapshotByPeriodBetweenTwoDates extends DbCleanerFilter {
  
  private final Date before;
  private final Date after;
  private GregorianCalendar calendar = new GregorianCalendar();
  private int lastFieldValue = -1;
  private final int dateField;
  
  public KeepOneSnapshotByPeriodBetweenTwoDates(int dateField, Date before, Date after){
    this.before = before;
    this.after = after;
    this.dateField = dateField;
  }

  @Override
  boolean filter(Snapshot snapshot) {
    boolean result = false;
    Date createdAt = snapshot.getCreatedAt();
    calendar.setTime(createdAt);
    int currentFieldValue = calendar.get(dateField);
    if (lastFieldValue != currentFieldValue && snapshot.getCreatedAt().after(after) && snapshot.getCreatedAt().before(before)) {
      result = true;
    }
    lastFieldValue = currentFieldValue;
    return result;
  }

}
