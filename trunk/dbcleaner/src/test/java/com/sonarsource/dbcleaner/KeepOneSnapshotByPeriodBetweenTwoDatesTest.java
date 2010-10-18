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
