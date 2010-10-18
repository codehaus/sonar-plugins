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
package com.sonarsource.dbcleaner;

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

import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;

public class DbCleanerPurge extends AbstractPurge {

  private static final Logger LOG = LoggerFactory.getLogger(DbCleanerPurge.class);
  private final DatabaseSession session;
  private final Project project;
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat();

  public DbCleanerPurge(DatabaseSession session, Project project) {
    super(session);
    this.session = session;
    this.project = project;
  }

  public void purge(PurgeContext context) {
    TimeProfiler profiler = new TimeProfiler().start("DbCleaner");

    Date dateToStartKeepingOneSnapshotByWeek = getDate(project.getConfiguration(),
        DbCleanerConstants.MONTHS_BEFORE_KEEPING_ONLY_ONE_SNAPSHOT_BY_WEEK, DbCleanerConstants._1_MONTH);
    LOG.info("Keep only one snapshot by week after : " + dateFormat.format(dateToStartKeepingOneSnapshotByWeek));
    Date dateToStartKeepingOneSnapshotByMonth = getDate(project.getConfiguration(),
        DbCleanerConstants.MONTHS_BEFORE_KEEPING_ONLY_ONE_SNAPSHOT_BY_MONTH, DbCleanerConstants._12_MONTH);
    LOG.info("Keep only one snapshot by month after : " + dateFormat.format(dateToStartKeepingOneSnapshotByMonth));
    Date dateToStartDeletingAllSnapshots = getDate(project.getConfiguration(),
        DbCleanerConstants.MONTHS_BEFORE_KEEPING_ONLY_ONE_SNAPSHOT_BY_MONTH, DbCleanerConstants._36_MONTH);
    LOG.info("Delete all snapshots after : " + dateFormat.format(dateToStartDeletingAllSnapshots));

    List<Snapshot> snapshotHistory = Lists.newLinkedList(getSnapshotHistoryOrderedByCreatedAt(context.getLastSnapshotId()));
    LOG.info("The project '" + project.getName() + "' has " + snapshotHistory.size() + " snapshots.");

    List<DbCleanerFilter> filters = Lists.newArrayList();
    filters.add(new KeepSnapshotsBetweenTwoDates(new Date(), dateToStartKeepingOneSnapshotByWeek));
    filters.add(new KeepSnapshotWithNewVersion());
    filters.add(new KeepOneSnapshotByPeriodBetweenTwoDates(GregorianCalendar.WEEK_OF_YEAR, dateToStartKeepingOneSnapshotByWeek,
        dateToStartKeepingOneSnapshotByMonth));
    filters.add(new KeepOneSnapshotByPeriodBetweenTwoDates(GregorianCalendar.MONTH, dateToStartKeepingOneSnapshotByMonth,
        dateToStartDeletingAllSnapshots));
    filters.add(new KeepLastSnapshot());

    for (DbCleanerFilter filter : filters) {
      filter.filter(snapshotHistory);
    }

    List<Integer> ids = getAllSnapshotIdsToPurge(snapshotHistory);
    LOG.info("There are " + snapshotHistory.size() + " snapshots and " + (ids.size() - snapshotHistory.size())
        + " children snapshots which are obsolete and are going to be deleted.");
    if (LOG.isDebugEnabled()) {
      DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
      for (Snapshot snapshot : snapshotHistory) {
        LOG.debug("Snapshot created at " + format.format(snapshot.getCreatedAt()));
      }
    }
    deleteSnapshotData(ids);
    profiler.stop();
  }

  protected void keepLastSnapshot(List<Snapshot> snapshotHistory) {
    ListIterator<Snapshot> iterator = snapshotHistory.listIterator();
    while (iterator.hasNext()) {
      Snapshot snapshot = iterator.next();
      if (snapshot.getLast()) {
        iterator.remove();
      }
    }
  }

  private List<Snapshot> getSnapshotHistoryOrderedByCreatedAt(int snapshotId) {
    Query query = session.createQuery("FROM " + Snapshot.class.getSimpleName()
        + " sp1 WHERE sp1.resourceId  = (select sp2.resourceId FROM " + Snapshot.class.getSimpleName()
        + " sp2 WHERE sp2.id = :id) order by sp1.createdAt");
    query.setParameter("id", snapshotId);
    return query.getResultList();
  }

  private List<Integer> getAllRelatedSnapshotIds(Snapshot rootSnapshot) {
    Query query = session.createQuery("select sp.id FROM " + Snapshot.class.getSimpleName()
        + " sp WHERE sp.rootId  = :rootId or id = :rootId");
    query.setParameter("rootId", rootSnapshot.getId());
    return query.getResultList();
  }

  protected Date getDate(Configuration conf, String propertyKey, String defaultNumberOfMonths) {
    int months = conf.getInt(propertyKey, Integer.parseInt(defaultNumberOfMonths));
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.add(GregorianCalendar.MONTH, -months);
    return calendar.getTime();
  }

  private List<Integer> getAllSnapshotIdsToPurge(List<Snapshot> rootSnapshots) {
    List<Integer> allIds = Lists.newArrayList();
    for (Snapshot snapshot : rootSnapshots) {
      allIds.addAll(getAllRelatedSnapshotIds(snapshot));
    }
    return allIds;
  }
}
