/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.dbcleaner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static com.sonarsource.dbcleaner.Utils.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.sonar.api.database.model.Snapshot;

public class KeepSnapshotsBetweenTwoDatesTest {

  @Test
  public void testFilter() {
    List<Snapshot> snapshots = new LinkedList<Snapshot>();
    snapshots.add(createSnapshot(1, day( -100)));
    snapshots.add(createSnapshot(2, day( -70)));
    snapshots.add(createSnapshot(3, day( -40)));
    snapshots.add(createSnapshot(4, day( -10)));

    assertThat(new KeepSnapshotsBetweenTwoDates(new Date(), day( -1)).filter(snapshots), is(0));
    assertThat(snapshots.size(), is(4));

    assertThat(new KeepSnapshotsBetweenTwoDates(new Date(), day( -80)).filter(snapshots), is(3));
    assertThat(snapshots.size(), is(1));
    assertThat(snapshots.get(0).getId(), is(1));
  }
}
