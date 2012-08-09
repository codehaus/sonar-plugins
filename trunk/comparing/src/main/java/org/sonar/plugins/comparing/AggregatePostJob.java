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

import org.sonar.plugins.comparing.dto.LanguageAggregateMeasureDTO;
import org.sonar.plugins.comparing.dto.LanguageGlobalMeasureDTO;

import org.sonar.plugins.comparing.database.MeasureByLanguageDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.database.DatabaseSession;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.MetricFinder;
import org.sonar.api.resources.Project;

import java.util.List;

public class AggregatePostJob implements PostJob {

  public static final Logger LOGGER = LoggerFactory.getLogger(AggregatePostJob.class.getName());

  private DatabaseSession session;
  private MeasureByLanguageDao measureDao;
  private MetricFinder metricFinder;

  public AggregatePostJob(DatabaseSession session, MetricFinder metricFinder) {
    this.session = session;
    this.metricFinder = metricFinder;
  }

  public void executeOn(Project prjct, SensorContext sc) {
    this.measureDao = new MeasureByLanguageDao(session, metricFinder);

    List<LanguageGlobalMeasureDTO> globalMeasures = measureDao.getGlobalMeasureByLanguage();

    // Save the number of projects by language
    measureDao.saveMeasure(ComparingMetrics.GLOBAL_PROJECT_KEY, buildGlobalData(globalMeasures, false));

    // Save the number of lines of code by language
    measureDao.saveMeasure(ComparingMetrics.GLOBAL_NCLOC_KEY, buildGlobalData(globalMeasures, true));
    // IMPORTANT: AGGREGATION MATCHES WITH COMPUTATION OF MIN, MAX AND AVERAGE VALUE
    // FOR ONE MEASURE FOR ALL PROJECT BY LANGUAGE

    // ============== SAVE THE SIZE METRICS ==============
    // Save the aggregation of number of lines of code by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_NCLOC_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.NCLOC_KEY)));

    // Save the aggregation of files by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_FILES_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.FILES_KEY)));

    // Save the aggregation of functions by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_FUNCTIONS_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.FUNCTIONS_KEY)));

    // Save the aggregation of functions by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_STATEMENTS_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.STATEMENTS_KEY)));

    // =========== SAVE THE COMPLEXITY METRICS ===========
    // Save the aggregation of complexity by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_COMPLEXITY_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.COMPLEXITY_KEY)));

    // Save the aggregation of function complexity by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_FUNCTION_COMPLEXITY_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.FUNCTION_COMPLEXITY_KEY)));

    // Save the aggregation of file complexity by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_FILE_COMPLEXITY_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.FILE_COMPLEXITY_KEY)));

    // ========== SAVE THE DUPLICATION METRICS ===========
    // Save the aggregation of duplicated lines density by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_DUPLICATED_LINES_DENSITY_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.DUPLICATED_LINES_DENSITY_KEY)));

    // Save the aggregation of duplicated lines by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_DUPLICATED_LINES_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.DUPLICATED_LINES_KEY)));

    // Save the aggregation of duplicated blocks by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_DUPLICATED_BLOCKS_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.DUPLICATED_BLOCKS_KEY)));

    // Save the aggregation of duplicated files by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_DUPLICATED_FILES_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.DUPLICATED_FILES_KEY)));

    // ========= SAVE THE DOCUMENTATION METRICS ==========
    // Save the aggregation of comment lines density by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_COMMENT_LINES_DENSITY_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.COMMENT_LINES_DENSITY_KEY)));

    // Save the aggregation of comment lines by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_COMMENT_LINES_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.COMMENT_LINES_KEY)));

    // Save the aggregation of public documented api density by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_PUBLIC_DOCUMENTED_API_DENSITY_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.PUBLIC_DOCUMENTED_API_DENSITY_KEY)));

    // Save the aggregation of public undocumented api by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_PUBLIC_UNDOCUMENTED_API_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.PUBLIC_UNDOCUMENTED_API_KEY)));

    // ============== SAVE THE RULES METRICS =============
    // Save the aggregation of violations density by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_VIOLATIONS_DENSITY_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.VIOLATIONS_DENSITY_KEY)));

    // Save the aggregation of violations by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_VIOLATIONS_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.VIOLATIONS_KEY)));

    // Save the aggregation of blocker violations by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_BLOCKER_VIOLATIONS_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.BLOCKER_VIOLATIONS_KEY)));

    // Save the aggregation of critical violations by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_CRITICAL_VIOLATIONS_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.CRITICAL_VIOLATIONS_KEY)));

    // Save the aggregation of major violations by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_MAJOR_VIOLATIONS_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.MAJOR_VIOLATIONS_KEY)));

    // Save the aggregation of minor violations by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_MINOR_VIOLATIONS_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.MINOR_VIOLATIONS_KEY)));

    // Save the aggregation of info violations by language
    measureDao.saveMeasure(ComparingMetrics.AGGREGATE_INFO_VIOLATIONS_KEY, buildAggregateData(
        measureDao.getAggregateMeasureByLanguage(CoreMetrics.INFO_VIOLATIONS_KEY)));

  }

  // ----------------------------------------------------------------------------------
  // | PRIVATE METHODS |
  // ----------------------------------------------------------------------------------

  private String buildGlobalData(final List<LanguageGlobalMeasureDTO> measures, final boolean loc) {
    String data = "";

    for (int i = 0; i < measures.size(); i++) {
      LanguageGlobalMeasureDTO measure = measures.get(i);
      if (loc) {
        data += measure.getLanguageName() + "=" + measure.getNbLOCs();
      } else {
        data += measure.getLanguageName() + "=" + measure.getNbProjects();
      }

      if (i != measures.size() - 1) {
        data += "|";
      }
    }

    return data;
  }

  private String buildAggregateData(final List<LanguageAggregateMeasureDTO> measures) {
    String data = "";

    for (int i = 0; i < measures.size(); i++) {
      LanguageAggregateMeasureDTO measure = measures.get(i);
      double averageValue = Math.round(measure.getAverageValue() * 10) / 10d;
      data += measure.getLanguageName() + "=" + averageValue + ";" + measure.getWorstValue()
        + ";" + measure.getBetterValue();

      if (i != measures.size() - 1) {
        data += "|";
      }
    }

    return data;
  }

}
