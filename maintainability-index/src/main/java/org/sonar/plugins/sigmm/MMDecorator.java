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

import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.ResourceUtils;

import java.util.*;

/**
 * {@inheritDoc}
 */
public final class MMDecorator implements org.sonar.api.batch.Decorator {

  /**
   * {@inheritDoc}
   */
  public boolean shouldExecuteOnProject(Project project) {
    // See SONARPLUGINS-190 to extend to other languages
    return project.getLanguage().equals(Java.INSTANCE);
  }

  @DependsUpon
  public List<Metric> dependsOnMetrics() {
    return Arrays.asList(CoreMetrics.NCLOC, CoreMetrics.DUPLICATED_LINES_DENSITY, CoreMetrics.COVERAGE);
  }

  @DependedUpon
  public List<Metric> generatesMetrics() {
    return Arrays.asList(MMMetrics.ANALYSABILITY, MMMetrics.CHANGEABILITY, MMMetrics.TESTABILITY, MMMetrics.STABILITY, MMMetrics.MAINTAINABILIY);
  }

  /**
   * {@inheritDoc}
   */
  public void decorate(Resource resource, DecoratorContext context) {
    if (!ResourceUtils.isRootProject(resource)) {
      return;
    }

    double ncloc = MeasureUtils.getValue(context.getMeasure(CoreMetrics.NCLOC), 0.0);
    double duplication = MeasureUtils.getValue(context.getMeasure(CoreMetrics.DUPLICATED_LINES_DENSITY), 0.0);
    double coverage = MeasureUtils.getValue(context.getMeasure(CoreMetrics.COVERAGE), 0.0);

    MMRank volumeRanking = computeVolumeRanking(ncloc);
    MMRank duplicationRanking = computeDuplicationRanking(duplication);
    MMRank complexityRanking = computeRankingFromMultipleLimits(MMMetricsDTO.complexityLines[0], MMMetricsDTO.complexityLines[1], MMMetricsDTO.complexityLines[2], ncloc);
    MMRank unitSizeRanking = computeRankingFromMultipleLimits(MMMetricsDTO.unitSizeLines[0], MMMetricsDTO.unitSizeLines[1], MMMetricsDTO.unitSizeLines[2],ncloc);
    MMRank unitTestingRanking = computeVolumeRanking(coverage);

//    System.out.println("volumeRanking :" + volumeRanking);
//    System.out.println("duplicationRanking :" + duplicationRanking);
//    System.out.println("complexityRanking :" + complexityRanking);
//    System.out.println("unitSizeRanking :" + unitSizeRanking);
//    System.out.println("unitTestingRanking :" + unitTestingRanking);

    MMRank testabilityRanking = MMRank.averageRank(complexityRanking, unitSizeRanking, unitTestingRanking);
    MMRank stabilityRanking = MMRank.averageRank(unitTestingRanking);
    MMRank changeabilityRanking = MMRank.averageRank(complexityRanking, duplicationRanking);
    MMRank analysabilityRanking = MMRank.averageRank(volumeRanking, duplicationRanking, unitSizeRanking, unitTestingRanking);
    
//    System.out.println("testability: " + testabilityRanking);
//    System.out.println("stability: " + stabilityRanking);
//    System.out.println("chaneability: " + changeabilityRanking);
//    System.out.println("analysability: " + analysabilityRanking);

    MMRank maintainabilityRanking = MMRank.averageRank(testabilityRanking, stabilityRanking, changeabilityRanking, analysabilityRanking);

    context.saveMeasure(MMMetrics.TESTABILITY, testabilityRanking.getValue());
    context.saveMeasure(MMMetrics.STABILITY, stabilityRanking.getValue());
    context.saveMeasure(MMMetrics.CHANGEABILITY, changeabilityRanking.getValue());
    context.saveMeasure(MMMetrics.ANALYSABILITY, analysabilityRanking.getValue());

    context.saveMeasure(MMMetrics.MAINTAINABILIY, maintainabilityRanking.getValue());
  }

  protected MMRank computeVolumeRanking(double value) {
    // Values for Java
    int[] bottomLimits = {1310000, 655000, 246000, 66000, 0};
    return findRangeValueBelongsTo(value, bottomLimits, true);
  }

  protected MMRank computeDuplicationRanking(double value) {
    int[] bottomLimits = {20, 10, 5, 3, 0};
    return findRangeValueBelongsTo(value, bottomLimits, true);
  }

  protected MMRank computeUnitTestRanking(double value) {
    int[] bottomLimits = {95, 80, 60, 20, 0};
    return findRangeValueBelongsTo(value, bottomLimits, false);
  }

//  protected MMRank computeComplexityRanking(double ncloc) {
//    int[] bottomLimits = {50, 20, 10};
//
//    int veryHigh = findNlocAboveComplexity(bottomLimits[0], 0);
//    int high = findNlocAboveComplexity(bottomLimits[1], veryHigh);
//    int moderate = findNlocAboveComplexity(bottomLimits[2], veryHigh + high);
//
//    return computeRankingFromMultipleLimits(veryHigh, high, moderate, ncloc);
//  }

//  private int findNlocAboveComplexity(int complexity, int alreadyCounted) {
//    int nclocAboveComplexity = 0;
//
///*    Collection<SquidUnit> methodsVeryHigh = squid.search(new QueryByType(SquidMethod.class), new QueryByMeasure(org.sonar.squid.measures.Metric.COMPLEXITY, QueryByMeasure.Operator.GREATER_THAN, complexity));
//    for (SquidUnit method : methodsVeryHigh) {
//      nclocAboveComplexity += method.getEndAtLine() - method.getStartAtLine();
//    }
//                                           */
//    nclocAboveComplexity -= alreadyCounted;
//    return nclocAboveComplexity;
//  }

//  protected MMRank computeUnitSizeRanking(double ncloc) {
//    int[] bottomLimits = {25, 10, 5};
//
//    int veryHigh = findNlocAboveNcloc(bottomLimits[0], 0);
//    int high = findNlocAboveNcloc(bottomLimits[1], veryHigh);
//    int moderate = findNlocAboveNcloc(bottomLimits[2], veryHigh + high);
//
//    return computeRankingFromMultipleLimits(veryHigh, high, moderate, ncloc);
//  }
//
//  private int findNlocAboveNcloc(int nclocThreshold, int alreadyCounted) {
//    int nclocAboveNcloc = 0;
//
//    /*Collection<SquidUnit> methodsVeryHigh = squid.search(new QueryByType(SquidMethod.class), new QueryByMeasure(org.sonar.squid.measures.Metric.COMPLEXITY, QueryByMeasure.Operator.GREATER_THAN, 0));
//    for (SquidUnit method : methodsVeryHigh) {
//      int ncloc = method.getEndAtLine() - method.getStartAtLine();
//      if (ncloc > nclocThreshold) {
//        nclocAboveNcloc += ncloc;
//      }
//    } */
//    nclocAboveNcloc -= alreadyCounted;
//    return nclocAboveNcloc;
//  }

  private MMRank computeRankingFromMultipleLimits(int veryHigh, int high, int moderate, double ncloc) {
    if (ncloc == 0) {
      return null;
    }

    int[] moderateLimits = {25, 30, 40, 50};
    int[] highLimits = {0, 5, 10, 15};
    int[] veryHighLimits = {0, 0, 0, 5};

    MMRank[] sortedRanks = MMRank.descSortedRanks();

    for (int i = 0; i < 4; i++) {
      if (moderate / ncloc * 100 < moderateLimits[i]
        && high / ncloc * 100 < highLimits[i]
        && veryHigh / ncloc * 100 < veryHighLimits[i]) {
        return sortedRanks[i];
      }
    }
    return MMRank.MINUSMINUS;
  }

  private MMRank findRangeValueBelongsTo(double value, int[] bottomLimits, boolean isAsc) {
    MMRank[] sortedRanks = isAsc ? MMRank.ascSortedRanks() : MMRank.descSortedRanks();
    for (int i = 0; i <= 4; i++) {
      if (value >= bottomLimits[i]) {
        return sortedRanks[i];
      }
    }
    return null;
  }


}
