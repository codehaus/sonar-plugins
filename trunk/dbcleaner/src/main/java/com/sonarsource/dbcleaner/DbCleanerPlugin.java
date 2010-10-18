/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.dbcleaner;

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import java.util.Arrays;
import java.util.List;

@Properties({
    @Property(key = DbCleanerConstants.MONTHS_BEFORE_KEEPING_ONLY_ONE_SNAPSHOT_BY_WEEK,
        defaultValue = DbCleanerConstants._1_MONTH, name = "Number of months before starting to keep only one snapshot by week",
        description = "After this number of months, if there are several snapshots during the same week, "
            + "the DbCleaner keeps the first one and fully delete the other ones.", global = true, project = true),
    @Property(key = DbCleanerConstants.MONTHS_BEFORE_KEEPING_ONLY_ONE_SNAPSHOT_BY_MONTH,
        defaultValue = DbCleanerConstants._12_MONTH, name = "Number of months before starting to keep only one snapshot by month",
        description = "After this number of months, if there are several snapshots during the same month, "
            + "the DbCleaner keeps the first one and fully delete the other ones.", global = true, project = true),
    @Property(key = DbCleanerConstants.MONTHS_BEFORE_DELETING_ALL_SNAPSHOTS, defaultValue = DbCleanerConstants._36_MONTH,
        name = "Number of months before starting to delete all remaining snapshots",
        description = "After this number of months, all snapshots are fully deleted.", global = true, project = true) })
public final class DbCleanerPlugin implements Plugin {

  public List getExtensions() {
    return Arrays.asList(DbCleanerPurge.class);
  }

  @Override
  public String toString() {
    return DbCleanerConstants.PLUGIN_NAME;
  }

  public String getKey() {
    return DbCleanerConstants.PLUGIN_KEY;
  }

  public String getName() {
    return DbCleanerConstants.PLUGIN_NAME;
  }

  public String getDescription() {
    return "The DbCleaner optimizes the Sonar DB performances by removing old and useless quality snapshots.";
  }
}
