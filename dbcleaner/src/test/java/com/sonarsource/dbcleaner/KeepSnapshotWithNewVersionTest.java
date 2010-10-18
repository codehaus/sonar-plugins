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

public class KeepSnapshotWithNewVersionTest {

  @Test
  public void testFilter() {
    List<Snapshot> snapshots = new LinkedList<Snapshot>();
    snapshots.add(createSnapshot(1, "0.1"));
    snapshots.add(createSnapshot(2, "0.1"));
    snapshots.add(createSnapshot(3, "0.2"));
    snapshots.add(createSnapshot(4, "0.2"));
    snapshots.add(createSnapshot(5, "0.3"));

    assertThat(new KeepSnapshotWithNewVersion().filter(snapshots), is(3));
    assertThat(snapshots.size(), is(2));
    assertThat(snapshots.get(0).getId(), is(2));
    assertThat(snapshots.get(1).getId(), is(4));
  }
}
