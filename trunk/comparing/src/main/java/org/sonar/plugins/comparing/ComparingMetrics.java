/*
 * Sonar Comparing Plugin
 * Copyright (C) 2012 David FRANCOIS
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.comparing;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.Arrays;
import java.util.List;

public class ComparingMetrics implements Metrics {

  public static final String GLOBAL_KEY = "global_";
  public static final String AGGREGATE_KEY = "aggregate_";

  /*
   * =======================================================
   * METRICS FOR GLOBAL WIDGET
   * =======================================================
   */

  public static final String GLOBAL_PROJECT_KEY = GLOBAL_KEY + "project";
  public static final Metric GLOBAL_PROJECT = new Metric.Builder(GLOBAL_PROJECT_KEY, "Global project",
      Metric.ValueType.DATA).setDescription("Global project by language")
      .setQualitative(false).setDomain("GLOBAL").create();

  public static final String GLOBAL_NCLOC_KEY = GLOBAL_KEY + CoreMetrics.NCLOC_KEY;
  public static final Metric GLOBAL_NCLOC = new Metric.Builder(GLOBAL_NCLOC_KEY, "Global ncloc",
      Metric.ValueType.DATA).setDescription("Global ncloc by language")
      .setQualitative(false).setDomain("GLOBAL").create();

  /*
   * =======================================================
   * METRICS FOR OVERVIEW PAGE
   * =======================================================
   */

  // ================== SIZE METRICS =======================
  public static final String AGGREGATE_NCLOC_KEY = AGGREGATE_KEY + CoreMetrics.NCLOC_KEY;
  public static final Metric AGGREGATE_NCLOC = new Metric.Builder(AGGREGATE_NCLOC_KEY, "Aggregate ncloc",
      Metric.ValueType.DATA).setDescription("Aggregation for ncloc by language")
      .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_FILES_KEY = AGGREGATE_KEY + CoreMetrics.FILES_KEY;
  public static final Metric AGGREGATE_FILES = new Metric.Builder(AGGREGATE_FILES_KEY, "Aggregate files",
      Metric.ValueType.DATA).setDescription("Aggregation for files by language")
      .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_FUNCTIONS_KEY = AGGREGATE_KEY + CoreMetrics.FUNCTIONS_KEY;
  public static final Metric AGGREGATE_FUNCTIONS = new Metric.Builder(AGGREGATE_FUNCTIONS_KEY, "Aggregate functions",
      Metric.ValueType.DATA).setDescription("Aggregation for functions by language")
      .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_STATEMENTS_KEY = AGGREGATE_KEY + CoreMetrics.STATEMENTS_KEY;
  public static final Metric AGGREGATE_STATEMENTS = new Metric.Builder(AGGREGATE_STATEMENTS_KEY, "Aggregate statements",
      Metric.ValueType.DATA).setDescription("Aggregation for statements by language")
      .setQualitative(false).setDomain("AGGREGATE").create();

  // ================ COMPLEXITY METRICS ===================
  public static final String AGGREGATE_COMPLEXITY_KEY = AGGREGATE_KEY + CoreMetrics.COMPLEXITY_KEY;
  public static final Metric AGGREGATE_COMPLEXITY = new Metric.Builder(AGGREGATE_COMPLEXITY_KEY,
      "Aggregate complexity", Metric.ValueType.DATA).setDescription("Aggregation for complexity by language")
      .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_FUNCTION_COMPLEXITY_KEY = AGGREGATE_KEY + CoreMetrics.FUNCTION_COMPLEXITY_KEY;
  public static final Metric AGGREGATE_FUNCTION_COMPLEXITY = new Metric.Builder(AGGREGATE_FUNCTION_COMPLEXITY_KEY,
      "Aggregate function complexity", Metric.ValueType.DATA)
      .setDescription("Aggregation for function complexity by language")
      .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_FILE_COMPLEXITY_KEY = AGGREGATE_KEY + CoreMetrics.FILE_COMPLEXITY_KEY;
  public static final Metric AGGREGATE_FILE_COMPLEXITY = new Metric.Builder(AGGREGATE_FILE_COMPLEXITY_KEY,
      "Aggregate file complexity", Metric.ValueType.DATA)
      .setDescription("Aggregation for file complexity by language")
      .setQualitative(false).setDomain("AGGREGATE").create();

  // =============== DUPLICATION METRICS ===================
  public static final String AGGREGATE_DUPLICATED_LINES_DENSITY_KEY = AGGREGATE_KEY
    + CoreMetrics.DUPLICATED_LINES_DENSITY_KEY;
  public static final Metric AGGREGATE_DUPLICATED_LINES_DENSITY =
      new Metric.Builder(AGGREGATE_DUPLICATED_LINES_DENSITY_KEY, "Aggregate duplicated lines density",
          Metric.ValueType.DATA).setDescription("Aggregation for duplicated lines density by language")
          .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_DUPLICATED_LINES_KEY = AGGREGATE_KEY
    + CoreMetrics.DUPLICATED_LINES_KEY;
  public static final Metric AGGREGATE_DUPLICATED_LINES =
      new Metric.Builder(AGGREGATE_DUPLICATED_LINES_KEY, "Aggregate duplicated lines",
          Metric.ValueType.DATA).setDescription("Aggregation for duplicated lines by language")
          .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_DUPLICATED_BLOCKS_KEY = AGGREGATE_KEY
    + CoreMetrics.DUPLICATED_BLOCKS_KEY;
  public static final Metric AGGREGATE_DUPLICATED_BLOCKS =
      new Metric.Builder(AGGREGATE_DUPLICATED_BLOCKS_KEY, "Aggregate duplicated blocks",
          Metric.ValueType.DATA).setDescription("Aggregation for duplicated blocks by language")
          .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_DUPLICATED_FILES_KEY = AGGREGATE_KEY
    + CoreMetrics.DUPLICATED_FILES_KEY;
  public static final Metric AGGREGATE_DUPLICATED_FILES =
      new Metric.Builder(AGGREGATE_DUPLICATED_FILES_KEY, "Aggregate duplicated files",
          Metric.ValueType.DATA).setDescription("Aggregation for duplicated files by language")
          .setQualitative(false).setDomain("AGGREGATE").create();

  // =============== DOCUMENTATION METRICS =================
  public static final String AGGREGATE_COMMENT_LINES_DENSITY_KEY = AGGREGATE_KEY
    + CoreMetrics.COMMENT_LINES_DENSITY_KEY;
  public static final Metric AGGREGATE_COMMENT_LINES_DENSITY =
      new Metric.Builder(AGGREGATE_COMMENT_LINES_DENSITY_KEY, "Aggregate comment lines density",
          Metric.ValueType.DATA).setDescription("Aggregation for comment lines density by language")
          .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_COMMENT_LINES_KEY = AGGREGATE_KEY
    + CoreMetrics.COMMENT_LINES_KEY;
  public static final Metric AGGREGATE_COMMENT_LINES =
      new Metric.Builder(AGGREGATE_COMMENT_LINES_KEY, "Aggregate comment lines",
          Metric.ValueType.DATA).setDescription("Aggregation for comment lines by language")
          .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_PUBLIC_DOCUMENTED_API_DENSITY_KEY = AGGREGATE_KEY
    + CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY;
  public static final Metric AGGREGATE_PUBLIC_DOCUMENTED_API_DENSITY =
      new Metric.Builder(AGGREGATE_PUBLIC_DOCUMENTED_API_DENSITY_KEY, "Aggregate public documented api density",
          Metric.ValueType.DATA).setDescription("Aggregation for public documented api density by language")
          .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_PUBLIC_UNDOCUMENTED_API_KEY = AGGREGATE_KEY
    + CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY;
  public static final Metric AGGREGATE_PUBLIC_UNDOCUMENTED_API =
      new Metric.Builder(AGGREGATE_PUBLIC_UNDOCUMENTED_API_KEY, "Aggregate public undocumented api",
          Metric.ValueType.DATA).setDescription("Aggregation for public undocumented api by language")
          .setQualitative(false).setDomain("AGGREGATE").create();

  // =================== RULES METRICS =====================
  public static final String AGGREGATE_VIOLATIONS_DENSITY_KEY = AGGREGATE_KEY
    + CoreMetrics.VIOLATIONS_DENSITY_KEY;
  public static final Metric AGGREGATE_VIOLATIONS_DENSITY =
      new Metric.Builder(AGGREGATE_VIOLATIONS_DENSITY_KEY, "Aggregate violations density",
          Metric.ValueType.DATA).setDescription("Aggregation for violations density by language")
          .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_VIOLATIONS_KEY = AGGREGATE_KEY
    + CoreMetrics.VIOLATIONS_KEY;
  public static final Metric AGGREGATE_VIOLATIONS =
      new Metric.Builder(AGGREGATE_VIOLATIONS_KEY, "Aggregate violations",
          Metric.ValueType.DATA).setDescription("Aggregation for violations by language")
          .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_BLOCKER_VIOLATIONS_KEY = AGGREGATE_KEY
    + CoreMetrics.BLOCKER_VIOLATIONS_KEY;
  public static final Metric AGGREGATE_BLOCKER_VIOLATIONS =
      new Metric.Builder(AGGREGATE_BLOCKER_VIOLATIONS_KEY, "Aggregate blocker violations",
          Metric.ValueType.DATA).setDescription("Aggregation for blocker violations by language")
          .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_CRITICAL_VIOLATIONS_KEY = AGGREGATE_KEY
    + CoreMetrics.CRITICAL_VIOLATIONS_KEY;
  public static final Metric AGGREGATE_CRITICAL_VIOLATIONS =
      new Metric.Builder(AGGREGATE_CRITICAL_VIOLATIONS_KEY, "Aggregate critical violations",
          Metric.ValueType.DATA).setDescription("Aggregation for critical violations by language")
          .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_MAJOR_VIOLATIONS_KEY = AGGREGATE_KEY
    + CoreMetrics.MAJOR_VIOLATIONS_KEY;
  public static final Metric AGGREGATE_MAJOR_VIOLATIONS =
      new Metric.Builder(AGGREGATE_MAJOR_VIOLATIONS_KEY, "Aggregate major violations",
          Metric.ValueType.DATA).setDescription("Aggregation for major violations by language")
          .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_MINOR_VIOLATIONS_KEY = AGGREGATE_KEY
    + CoreMetrics.MINOR_VIOLATIONS_KEY;
  public static final Metric AGGREGATE_MINOR_VIOLATIONS =
      new Metric.Builder(AGGREGATE_MINOR_VIOLATIONS_KEY, "Aggregate minor violations",
          Metric.ValueType.DATA).setDescription("Aggregation for minor violations by language")
          .setQualitative(false).setDomain("AGGREGATE").create();

  public static final String AGGREGATE_INFO_VIOLATIONS_KEY = AGGREGATE_KEY
    + CoreMetrics.INFO_VIOLATIONS_KEY;
  public static final Metric AGGREGATE_INFO_VIOLATIONS =
      new Metric.Builder(AGGREGATE_INFO_VIOLATIONS_KEY, "Aggregate info violations",
          Metric.ValueType.DATA).setDescription("Aggregation for info violations by language")
          .setQualitative(false).setDomain("AGGREGATE").create();

  public List<Metric> getMetrics() {
    return Arrays.asList(GLOBAL_NCLOC, GLOBAL_PROJECT,
        AGGREGATE_NCLOC, AGGREGATE_FILES, AGGREGATE_FUNCTIONS, AGGREGATE_STATEMENTS,
        AGGREGATE_COMPLEXITY, AGGREGATE_FUNCTION_COMPLEXITY, AGGREGATE_FILE_COMPLEXITY,
        AGGREGATE_DUPLICATED_LINES_DENSITY, AGGREGATE_DUPLICATED_LINES,
        AGGREGATE_DUPLICATED_BLOCKS, AGGREGATE_DUPLICATED_FILES,
        AGGREGATE_COMMENT_LINES_DENSITY, AGGREGATE_PUBLIC_DOCUMENTED_API_DENSITY,
        AGGREGATE_COMMENT_LINES, AGGREGATE_PUBLIC_UNDOCUMENTED_API,
        AGGREGATE_VIOLATIONS_DENSITY, AGGREGATE_VIOLATIONS, AGGREGATE_BLOCKER_VIOLATIONS,
        AGGREGATE_CRITICAL_VIOLATIONS, AGGREGATE_MAJOR_VIOLATIONS, AGGREGATE_MINOR_VIOLATIONS,
        AGGREGATE_INFO_VIOLATIONS);
  }

}
