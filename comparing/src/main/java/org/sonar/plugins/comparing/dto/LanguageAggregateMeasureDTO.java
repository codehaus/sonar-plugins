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
package org.sonar.plugins.comparing.dto;

public class LanguageAggregateMeasureDTO {

  private String languageName;

  private Double averageValue;

  private Double worstValue;

  private Double betterValue;

  public LanguageAggregateMeasureDTO(final String languageName, final Double averageValue,
      final Double worstValue, final Double betterValue) {
    this.languageName = languageName;
    this.averageValue = averageValue;
    this.worstValue = worstValue;
    this.betterValue = betterValue;
  }

  /**
   * @return the languageName
   */
  public String getLanguageName() {
    return languageName;
  }

  /**
   * @return the averageValue
   */
  public Double getAverageValue() {
    return averageValue;
  }

  /**
   * @return the worstValue
   */
  public Double getWorstValue() {
    return worstValue;
  }

  /**
   * @return the betterValue
   */
  public Double getBetterValue() {
    return betterValue;
  }

}
