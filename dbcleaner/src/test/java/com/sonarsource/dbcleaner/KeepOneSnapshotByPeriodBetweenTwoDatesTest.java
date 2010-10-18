/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.dbcleaner;

import static com.sonarsource.dbcleaner.Utils.createSnapshot;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.sonar.api.database.model.Snapshot;

public class KeepOneSnapshotByPeriodBetweenTwoDatesTest {

  @Test
  public void testFilter() {
    List<Snapshot> snapshots = new LinkedList<Snapshot>();
    snapshots.add(Utils.createSnapshot(1, Utils.week( -7, 1)));
    snapshots.add(Utils.createSnapshot(2, Utils.week( -7, 2)));
    snapshots.add(Utils.createSnapshot(3, Utils.week( -7, 3)));
    snapshots.add(Utils.createSnapshot(4, Utils.week( -6, 3)));
    snapshots.add(Utils.createSnapshot(5, Utils.week( -6, 4)));

    assertThat(new KeepOneSnapshotByPeriodBetweenTwoDates(GregorianCalendar.WEEK_OF_YEAR, Utils.week( -3, 1), Utils.week( -9, 1)).filter(snapshots), is(2));
    assertThat(snapshots.size(), is(3));
    assertThat(snapshots.get(0).getId(), is(2));
    assertThat(snapshots.get(1).getId(), is(3));
    assertThat(snapshots.get(2).getId(), is(5));
  }
}
