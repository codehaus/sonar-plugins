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
package org.sonar.plugins.comparing.database;

import org.sonar.plugins.comparing.dto.LanguageAggregateMeasureDTO;
import org.sonar.plugins.comparing.dto.LanguageGlobalMeasureDTO;

import org.sonar.api.measures.Metric;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper to instantiate DTO List since Hibernate query results.
 */
public class MeasureByLanguageMapper {

  private MeasureByLanguageMapper() {
    // Don't instantiate this Singleton
  }

  /**
   * Map all results of global request to list of LanguageGlobalMeasureDTO.
   * 
   * @param queryResults All results of global request
   * @return List List of LanguageGlobalMeasureDTO
   */
  public static List<LanguageGlobalMeasureDTO> mapGlobalResultsToDTOList(List<Object[]> queryResults) {
    List<LanguageGlobalMeasureDTO> result = null;

    if (queryResults != null) {
      result = new ArrayList<LanguageGlobalMeasureDTO>();
      for (Object[] queryResult : queryResults) {
        result.add(mapGlobalResultToDTO(queryResult));
      }
    }

    return result;
  }

  /**
   * Map all results of aggregate request to list of LanguageAggregateMeasureDTO.
   * 
   * @param queryResults All results of aggregate request
   * @param metric The metric to compute the worst and better value
   * @return List List of LanguageAggregateMeasureDTO
   */
  public static List<LanguageAggregateMeasureDTO> mapAggregateResultsToDTOList(List<Object[]> queryResults,
      Metric metric) {
    List<LanguageAggregateMeasureDTO> result = null;

    if (queryResults != null) {
      result = new ArrayList<LanguageAggregateMeasureDTO>();
      for (Object[] queryResult : queryResults) {
        result.add(mapAggregateResultToDTO(queryResult, metric));
      }
    }

    return result;
  }

  /**
   * Map one result of global request (queryResult) to LanguageGlobalMeasureDTO
   * One result is a table with :
   * - position 0 : language name
   * - position 1 : the global number of lines of code for the language
   * - position 2 : the global number of projects for the language
   * 
   * @param queryResult One result of global request
   * @return  LanguageGlobalMeasureDTO Mapped DTO
   */
  public static LanguageGlobalMeasureDTO mapGlobalResultToDTO(Object[] queryResult) {
    return new LanguageGlobalMeasureDTO((String) queryResult[0], Long.valueOf(((Double) queryResult[1]).longValue()),
        Integer.valueOf(((Long) queryResult[2]).intValue()));
  }

  /**
   * Map one result of aggregate request (queryResult) to LanguageAggregateMeasureDTO
   * One result is a table with :
   * - position 0 : language name
   * - position 1 : the average value for the language name and for one metric
   * - position 2 : the worst value for the language name and for one metric
   * - position 3 : the better value for the language name and for one metric
   * 
   * @param queryResult One result of aggregate request
   * @param metric The metric to compute the worst and better value
   * @return LanguageAggregateMeasureDTO Mapped DTO
   */
  public static LanguageAggregateMeasureDTO mapAggregateResultToDTO(Object[] queryResult, Metric metric) {
    Double worstValue = null;
    Double betterValue = null;
    if (metric.getQualitative() && metric.getDirection() < 0) {
      worstValue = (Double) queryResult[3];
      betterValue = (Double) queryResult[2];
    } else {
      worstValue = (Double) queryResult[2];
      betterValue = (Double) queryResult[3];
    }
    return new LanguageAggregateMeasureDTO((String) queryResult[0], (Double) queryResult[1],
        worstValue, betterValue);
  }
}
