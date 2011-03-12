/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
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

package org.sonar.plugins.sigmm;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class MMRankTest {

  @Test
  public void testNumberOfElementsInMMRank() {
    assertEquals(MMRank.values().length, 5);
  }

  @Test
  public void testAscendingList() {
    MMRank[] mmr = MMRank.ascSortedRanks();
    assertEquals(mmr.length, 5);
    assertEquals(mmr[0], MMRank.MINUSMINUS);
    assertEquals(mmr[4], MMRank.PLUSPLUS);
  }

  @Test
  public void testDescendingList() {
    MMRank[] mmr = MMRank.descSortedRanks();
    assertEquals(mmr.length, 5);
    assertEquals(mmr[0], MMRank.PLUSPLUS);
    assertEquals(mmr[4], MMRank.MINUSMINUS);
  }

  @Test
  public void testValueForRank() {
    assertEquals(-2.0, MMRank.MINUSMINUS.getValue(), 0);
    assertEquals(1.0, MMRank.PLUS.getValue(), 0);
  }

  @Test
  public void testRankForValue() {
    assertEquals(MMRank.getRank(2.0), MMRank.PLUSPLUS);
    assertEquals(MMRank.getRank(0.0), MMRank.ZERO);
  }

  @Test
  public void testAverageRanking() {
    assertNull(MMRank.averageRank());
    assertEquals(MMRank.averageRank(MMRank.MINUS, MMRank.MINUS), MMRank.MINUS);
    assertEquals(MMRank.averageRank(MMRank.MINUS, MMRank.PLUS), MMRank.ZERO);
    assertEquals(MMRank.averageRank(MMRank.ZERO, MMRank.PLUS), MMRank.PLUS);
    assertEquals(MMRank.averageRank(MMRank.ZERO, MMRank.MINUS), MMRank.ZERO);
    assertEquals(MMRank.averageRank(MMRank.MINUSMINUS, MMRank.ZERO, MMRank.PLUS), MMRank.ZERO);
  }

}
