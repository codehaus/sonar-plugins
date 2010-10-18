/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.dbcleaner;

import org.sonar.api.database.model.Snapshot;


public class KeepLastSnapshot extends DbCleanerFilter {
  
  @Override
  boolean filter(Snapshot snapshot) {
    return snapshot.getLast();
  }

}
