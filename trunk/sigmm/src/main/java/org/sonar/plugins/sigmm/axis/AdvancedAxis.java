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

package org.sonar.plugins.sigmm.axis;

import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.utils.KeyValueFormat;
import org.sonar.plugins.sigmm.MMRank;

import java.util.Map;

public class AdvancedAxis implements MMAxis {
  private Metric metric;
  private Number[] bottomLimits;
  private DecoratorContext context;

  public AdvancedAxis(Number[] bottomLimits, DecoratorContext context, Metric metric) {
    this.metric = metric;
    this.bottomLimits = bottomLimits;
    this.context = context;
  }

  public MMRank getRank() {
    double ncloc = MeasureUtils.getValue(context.getMeasure(CoreMetrics.NCLOC), 0.0);

    if (ncloc == 0) {
      return null;
    }
    Measure measure = context.getMeasure(metric);

    if (!MeasureUtils.hasData(measure)) {
      return null;
    }
    Map<Integer, Integer> map = KeyValueFormat.parse(measure.getData(), new KeyValueFormat.IntegerNumbersPairTransformer());

    int[] moderateLimits = { 25, 30, 40, 50 };
    int[] highLimits = { 0, 5, 10, 15 };
    int[] veryHighLimits = { 0, 0, 0, 5 };

    MMRank[] sortedRanks = MMRank.descSortedRanks();

    for (int i = 0; i < 4; i++) {
      if (map.get(Integer.valueOf(bottomLimits[2].intValue())) / ncloc * 100 <= moderateLimits[i]
          && map.get(Integer.valueOf(bottomLimits[1].intValue())) / ncloc * 100 <= highLimits[i]
          && map.get(Integer.valueOf(bottomLimits[0].intValue())) / ncloc * 100 <= veryHighLimits[i]) {
        return sortedRanks[i];
      }
    }
    return MMRank.MINUSMINUS;
  }

}
