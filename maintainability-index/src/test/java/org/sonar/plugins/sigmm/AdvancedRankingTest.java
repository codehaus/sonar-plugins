/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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
import static org.junit.Assert.assertNull;

public class AdvancedRankingTest {

  @Test
  public void testCCMeasures() {
    MMDecorator decorator = new MMDecorator();
    assertEquals(MMRank.PLUSPLUS, decorator.computeVolumeRanking(0));
    assertEquals(MMRank.PLUSPLUS, decorator.computeVolumeRanking(65999));

    assertEquals(MMRank.PLUS, decorator.computeVolumeRanking(66000));
    assertEquals(MMRank.PLUS, decorator.computeVolumeRanking(245999));

    assertEquals(MMRank.ZERO, decorator.computeVolumeRanking(246000));
    assertEquals(MMRank.ZERO, decorator.computeVolumeRanking(654999));

    assertEquals(MMRank.MINUS, decorator.computeVolumeRanking(655000));
    assertEquals(MMRank.MINUS, decorator.computeVolumeRanking(1309999));

    assertEquals(MMRank.MINUSMINUS, decorator.computeVolumeRanking(1310000));
    assertEquals(MMRank.MINUSMINUS, decorator.computeVolumeRanking(100000000));

    assertNull(decorator.computeVolumeRanking(-100));
  }
}

