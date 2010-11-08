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
package org.sonar.plugins.dbcleaner;

import com.google.common.collect.Lists;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.PurgeContext;
import org.sonar.api.database.DatabaseSession;
import org.sonar.api.database.model.Snapshot;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.TimeProfiler;
import org.sonar.core.purge.AbstractPurge;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DbCleanerRunner extends AbstractPurge {

  private static final Logger LOG = LoggerFactory.getLogger(DbCleanerRunner.class);
  private final DbCleanerSqlRequests sql;
  private final Project project;
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat();

  Date dateToStartKeepingOneSnapshotByWeek;
  Date dateToStartKeepingOneSnapshotByMonth;
  Date dateToStartDeletingAllSnapshots;

  public DbCleanerRunner(DatabaseSession session, Project project) {
    super(session);
    this.sql = new DbCleanerSqlRequests(session);
    this.project = project;
    initMilestones();
  }

  public void purge(PurgeContext context) {
    purge(context.getLastSnapshotId());
  }

  public void purge(int snapshotId) {
    TimeProfiler profiler = new TimeProfiler().start("DbCleaner");

    List<DbCleanerFilter> filters = initDbCleanerFilters();
    List<Snapshot> snapshotHistory = getAllProjectSnapshots(snapshotId);
    applyFilters(snapshotHistory, filters);
    deleteSnapshotsAndAllRelatedData(snapshotHistory);

    profiler.stop();
  }

  private List<Snapshot> getAllProjectSnapshots(int snapshotId) {
    List<Snapshot> snapshotHistory = Lists.newLinkedList(sql.getProjectSnapshotsOrderedByCreatedAt(snapshotId));
    LOG.info("The project '" + project.getName() + "' has " + snapshotHistory.size() + " snapshots.");
    return snapshotHistory;
  }

  private void deleteSnapshotsAndAllRelatedData(List<Snapshot> snapshotHistory) {
    if (snapshotHistory.isEmpty()) {
      LOG.info("There are no snapshots to purge");
      return;
    }
    List<Integer> ids = Lists.newArrayList();
    for (Snapshot snapshot : snapshotHistory) {
      ids.addAll(sql.getChildIds(snapshot));
    }
    LOG.info("There are " + snapshotHistory.size() + " snapshots and " + (ids.size() - snapshotHistory.size())
        + " children snapshots which are obsolete and are going to be deleted.");
    if (LOG.isDebugEnabled()) {
      DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
      for (Snapshot snapshot : snapshotHistory) {
        LOG.debug("Delete snapshot created at " + format.format(snapshot.getCreatedAt()));
      }
    }
    deleteSnapshotData(ids);
  }

  private void applyFilters(List<Snapshot> snapshotHistory, List<DbCleanerFilter> filters) {
    for (DbCleanerFilter filter : filters) {
      filter.filter(snapshotHistory);
    }
  }

  private void initMilestones() {
    dateToStartKeepingOneSnapshotByWeek = getDate(project.getConfiguration(),
        DbCleanerConstants.MONTHS_BEFORE_KEEPING_ONLY_ONE_SNAPSHOT_BY_WEEK, DbCleanerConstants._1_MONTH);
    LOG.debug("Keep only one snapshot by week after : " + dateFormat.format(dateToStartKeepingOneSnapshotByWeek));
    dateToStartKeepingOneSnapshotByMonth = getDate(project.getConfiguration(),
        DbCleanerConstants.MONTHS_BEFORE_KEEPING_ONLY_ONE_SNAPSHOT_BY_MONTH, DbCleanerConstants._12_MONTH);
    LOG.debug("Keep only one snapshot by month after : " + dateFormat.format(dateToStartKeepingOneSnapshotByMonth));
    dateToStartDeletingAllSnapshots = getDate(project.getConfiguration(), DbCleanerConstants.MONTHS_BEFORE_DELETING_ALL_SNAPSHOTS,
        DbCleanerConstants._36_MONTH);
    LOG.debug("Delete all snapshots after : " + dateFormat.format(dateToStartDeletingAllSnapshots));
  }

  private List<DbCleanerFilter> initDbCleanerFilters() {
    List<DbCleanerFilter> filters = Lists.newArrayList();
    filters.add(new KeepLibrarySnapshot());
    filters.add(new KeepSnapshotsBetweenTwoDates(new Date(), dateToStartKeepingOneSnapshotByWeek));
    filters.add(new KeepSnapshotWithNewVersion(dateToStartDeletingAllSnapshots));
    filters.add(new KeepOneSnapshotByPeriodBetweenTwoDates(GregorianCalendar.WEEK_OF_YEAR, dateToStartKeepingOneSnapshotByWeek,
        dateToStartKeepingOneSnapshotByMonth));
    filters.add(new KeepOneSnapshotByPeriodBetweenTwoDates(GregorianCalendar.MONTH, dateToStartKeepingOneSnapshotByMonth,
        dateToStartDeletingAllSnapshots));
    filters.add(new KeepLastSnapshot());
    return filters;
  }

  protected Date getDate(Configuration conf, String propertyKey, String defaultNumberOfMonths) {
    int months = conf.getInt(propertyKey, Integer.parseInt(defaultNumberOfMonths));
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.add(GregorianCalendar.MONTH, -months);
    return calendar.getTime();
  }
}
