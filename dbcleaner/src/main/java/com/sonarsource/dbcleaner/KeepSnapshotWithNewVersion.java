/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.dbcleaner;

import org.sonar.api.database.model.Snapshot;

public class KeepSnapshotWithNewVersion extends DbCleanerFilter {

  private String lastSnapshotVersion = null;

  @Override
  boolean filter(Snapshot snapshot) {
    boolean result = false;
    String snapshotVersion = (snapshot.getVersion() != null ? snapshot.getVersion() : "");
    if ( !snapshotVersion.equals(lastSnapshotVersion)) {
      result = true;
    }
    lastSnapshotVersion = snapshotVersion;
    return result;
  }

}
