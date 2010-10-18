/*
 * Copyright (C) 2010 SonarSource SA
 * All rights reserved
 * mailto:contact AT sonarsource DOT com
 */
package com.sonarsource.dbcleaner;

import static com.sonarsource.dbcleaner.Utils.createSnapshot;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.sonar.api.database.model.Snapshot;

public class KeepLastSnapshotTest {

  @Test
  public void testFilter() {
    List<Snapshot> snapshots = new LinkedList<Snapshot>();
    snapshots.add(createSnapshot(1, "0.1"));
    Snapshot lastSnapshot = createSnapshot(2, "0.1");
    lastSnapshot.setLast(true);
    snapshots.add(lastSnapshot);

    assertThat(new KeepLastSnapshot().filter(snapshots), is(1));
    assertThat(snapshots.size(), is(1));
    assertThat(snapshots.get(0).getId(), is(1));
  }
}
