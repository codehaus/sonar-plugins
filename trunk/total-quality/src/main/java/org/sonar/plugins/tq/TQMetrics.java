/*
 * Sonar Total Quality Plugin, open source software quality management tool.
 * Copyright (C) 2010 
 * mailto:e72636 AT gmail DTO com
 *
 * Sonar Total Quality Plugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar Total Quality Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.tq;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import com.google.common.collect.ImmutableMap;

/** Metrics for TQ plugin. */
public final class TQMetrics implements Metrics {

  /** Domain Architecture. */
  public static String DOMAIN_ARCHITECTURE = "Architecture";

  public static final String TQ_TOTAL_QUALITY_KEY = "tq-total-quality";
  public static final Metric TQ_TOTAL_QUALITY = new Metric(TQ_TOTAL_QUALITY_KEY, "Total Quality", "The total quality of a project",
      Metric.ValueType.PERCENT, Metric.DIRECTION_BETTER, true, CoreMetrics.DOMAIN_GENERAL);

  public static final String TQ_DRY_KEY = "tq-dry";
  public static final Metric TQ_DRY = new Metric(TQ_DRY_KEY, "DRYness", "DRY = 100 - " + CoreMetrics.DUPLICATED_LINES_DENSITY_KEY,
      Metric.ValueType.PERCENT, Metric.DIRECTION_BETTER, true, CoreMetrics.DOMAIN_DUPLICATION);

  public static final String TQ_CODE_KEY = "tq-code";
  public static final Metric TQ_CODE = new Metric(TQ_CODE_KEY, "Code Quality", "CODE quality from "
      + CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY + ", " + CoreMetrics.VIOLATIONS_DENSITY_KEY + " and " + TQ_DRY_KEY,
      Metric.ValueType.PERCENT, Metric.DIRECTION_BETTER, true, CoreMetrics.DOMAIN_RULES);

  public static final String TQ_TS_KEY = "tq-ts";
  public static final Metric TQ_TS = new Metric(TQ_TS_KEY, "Testing Quality", "TS quality from " + CoreMetrics.BRANCH_COVERAGE_KEY + ", "
      + CoreMetrics.LINE_COVERAGE_KEY + " and " + CoreMetrics.COVERAGE_KEY, Metric.ValueType.PERCENT, Metric.DIRECTION_BETTER, true,
      CoreMetrics.DOMAIN_TESTS);

  public static final String TQ_DESIGN_NOM_KEY = "tq-design-nom";
  public static final Metric TQ_DESIGN_NOM = new Metric(TQ_DESIGN_NOM_KEY, "Design Classes and Methods Complexity",
      "NOM from classes with " + CoreMetrics.FUNCTION_COMPLEXITY_KEY + " gt value (2.5 default) and " + CoreMetrics.CLASS_COMPLEXITY_KEY
          + " gt value (12 default)", Metric.ValueType.PERCENT, Metric.DIRECTION_BETTER, true, CoreMetrics.DOMAIN_DESIGN);

  public static final String TQ_DESIGN_LCOM4_KEY = "tq-design-lcom4";
  public static final Metric TQ_DESIGN_LCOM4 = new Metric(TQ_DESIGN_LCOM4_KEY, "Design Lack of Cohesion of Methods",
      "LCOM4 from classes with " + CoreMetrics.LCOM4_KEY + " gt vale (50 default)", Metric.ValueType.PERCENT, Metric.DIRECTION_BETTER,
      true, CoreMetrics.DOMAIN_DESIGN);

  public static final String TQ_DESIGN_RFC_KEY = "tq-design-rfc";
  public static final Metric TQ_DESIGN_RFC = new Metric(TQ_DESIGN_RFC_KEY, "Design Response for Class", "RFC from classes with "
      + CoreMetrics.RFC_KEY + " gt vale (50 default)", Metric.ValueType.PERCENT, Metric.DIRECTION_BETTER, true, CoreMetrics.DOMAIN_DESIGN);

  public static final String TQ_DESIGN_CBO_KEY = "tq-design-cbo";
  public static final Metric TQ_DESIGN_CBO = new Metric(TQ_DESIGN_CBO_KEY, "Design Coupling Between Objects", "CBO from classes with "
      + CoreMetrics.EFFERENT_COUPLINGS_KEY + " gt value (5 default)", Metric.ValueType.PERCENT, Metric.DIRECTION_BETTER, true,
      CoreMetrics.DOMAIN_DESIGN);

  public static final String TQ_DESIGN_DIT_KEY = "tq-design-dit";
  public static final Metric TQ_DESIGN_DIT = new Metric(TQ_DESIGN_DIT_KEY, "Design Depth of Inheritance Tree", "DIT from classes with "
      + CoreMetrics.DEPTH_IN_TREE_KEY + " gt value (5 default)", Metric.ValueType.PERCENT, Metric.DIRECTION_BETTER, true,
      CoreMetrics.DOMAIN_DESIGN);

  public static final String TQ_DESIGN_KEY = "tq-design";
  public static final Metric TQ_DESIGN = new Metric(TQ_DESIGN_KEY, "Design Quality", "DES from " + TQ_DESIGN_DIT_KEY + ", "
      + TQ_DESIGN_CBO_KEY + ", " + TQ_DESIGN_LCOM4_KEY + ", " + TQ_DESIGN_RFC_KEY + " and " + TQ_DESIGN_NOM_KEY, Metric.ValueType.PERCENT,
      Metric.DIRECTION_BETTER, true, CoreMetrics.DOMAIN_DESIGN);

  public static final String TQ_ARCHITECTURE_COH_KEY = "tq-architecture-coh";
  public static final Metric TQ_ARCHITECTURE_COH = new Metric(TQ_ARCHITECTURE_COH_KEY, "Architecture Cohesion",
      "COH from inverse of files or packages with cycles", Metric.ValueType.PERCENT, Metric.DIRECTION_BETTER, true, DOMAIN_ARCHITECTURE);

  public static final String TQ_ARCHITECTURE_ADI_KEY = "tq-architecture-adi";
  public static final Metric TQ_ARCHITECTURE_ADI = new Metric("isoqa_architecture_adi", "Architecture Distance", "ADI from "
      + CoreMetrics.DISTANCE_KEY + " gt value (20 default)", Metric.ValueType.PERCENT, Metric.DIRECTION_BETTER, true, DOMAIN_ARCHITECTURE);

  public static final String TQ_ARCHITECTURE_KEY = "tq-architecture";
  public static final Metric TQ_ARCHITECTURE = new Metric(TQ_ARCHITECTURE_KEY, "Architecture", "ARCH from " + TQ_ARCHITECTURE_ADI_KEY
      + " and " + TQ_ARCHITECTURE_COH_KEY, Metric.ValueType.PERCENT, Metric.DIRECTION_BETTER, true, DOMAIN_ARCHITECTURE);

  public List<Metric> getMetrics() {
    return Arrays.asList(TQ_TOTAL_QUALITY, TQ_DRY, TQ_CODE, TQ_TS, TQ_DESIGN_NOM, TQ_DESIGN_RFC, TQ_DESIGN_CBO, TQ_DESIGN_DIT,
        TQ_DESIGN_LCOM4, TQ_DESIGN, TQ_ARCHITECTURE_ADI, TQ_ARCHITECTURE_COH, TQ_ARCHITECTURE);
  }

  public static final Map<String, Metric> formulaParams = ImmutableMap.<String, Metric> builder().put("ARCH", TQ_ARCHITECTURE).put(
      "DESIGN", TQ_DESIGN).put("CODE", TQ_CODE).put("TESTS", TQ_TS).put("NOM", TQ_DESIGN_NOM).put("LCOM", TQ_DESIGN_LCOM4).put("RFC",
      TQ_DESIGN_RFC).put("CBO", TQ_DESIGN_CBO).put("DIT", TQ_DESIGN_DIT).put("COH", TQ_ARCHITECTURE_COH).put("ADI", TQ_ARCHITECTURE_ADI)
      .put("COV", CoreMetrics.COVERAGE).put("LINE", CoreMetrics.BRANCH_COVERAGE).put("BRAN", CoreMetrics.LINE_COVERAGE).put("DOC",
          CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY).put("RULES", CoreMetrics.VIOLATIONS_DENSITY).put("DRY", TQ_DRY).build();

}
