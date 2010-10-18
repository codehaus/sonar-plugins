/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.dbcleaner;

import java.util.List;
import java.util.ListIterator;

import org.sonar.api.database.model.Snapshot;

abstract class DbCleanerFilter {

  final int filter(List<Snapshot> snapshots) {
    int before = snapshots.size();
    ListIterator<Snapshot> iterator = snapshots.listIterator();
    while (iterator.hasNext()) {
      Snapshot snapshot = iterator.next();
      if(filter(snapshot)){
        iterator.remove();
      }
    }
    int after = snapshots.size();
    return before - after;
  }

  abstract boolean filter(Snapshot snapshot);
}
