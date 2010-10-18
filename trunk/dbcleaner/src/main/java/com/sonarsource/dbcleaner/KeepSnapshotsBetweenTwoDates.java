/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.dbcleaner;

import java.util.Date;

import org.sonar.api.database.model.Snapshot;


public class KeepSnapshotsBetweenTwoDates extends DbCleanerFilter {
  
  private Date before;
  private Date after;
  
  public KeepSnapshotsBetweenTwoDates(Date before, Date after){
    this.before = before;
    this.after = after;
  }

  @Override
  boolean filter(Snapshot snapshot) {
    Date createdAt = snapshot.getCreatedAt();
    return createdAt.before(before) && createdAt.after(after);
  }

}
