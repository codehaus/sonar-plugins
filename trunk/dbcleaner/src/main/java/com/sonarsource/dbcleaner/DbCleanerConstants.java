/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.dbcleaner;

public interface DbCleanerConstants {
  
  String PLUGIN_KEY = "dbcleaner";
  String PLUGIN_NAME = "DbCleaner";
  String MONTHS_BEFORE_KEEPING_ONLY_ONE_SNAPSHOT_BY_WEEK = "sonar.dbcleaner.monthsBeforeKeepingOnlyOneSnapshotByWeek";
  String MONTHS_BEFORE_KEEPING_ONLY_ONE_SNAPSHOT_BY_MONTH = "sonar.dbcleaner.monthsBeforeKeepingOnlyOneSnapshotByMonth";
  String MONTHS_BEFORE_DELETING_ALL_SNAPSHOTS = "sonar.dbcleaner.monthsBeforeDeletingAllSnapshots";
  String _1_MONTH = "1";
  String _12_MONTH = "12";
  String _36_MONTH = "36";
}
