/*
 * Sonar JaCoCo Plugin
 * Copyright (C) 2010 SonarSource
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

package org.sonar.plugins.jacoco.itcoverage.viewer.client;

/**
 * Should be in {@link org.sonar.gwt.Metrics}
 */
public interface Metrics {
  String IT_COVERAGE = "coverage";
  String IT_LINES_TO_COVER = "it_lines_to_cover";
  String IT_UNCOVERED_LINES = "it_uncovered_lines";
  String IT_LINE_COVERAGE = "it_line_coverage";
  String IT_COVERAGE_LINE_HITS_DATA = "it_coverage_line_hits_data";
  String IT_CONDITIONS_TO_COVER = "it_conditions_to_cover";
  String IT_UNCOVERED_CONDITIONS = "it_uncovered_conditions";
  String IT_BRANCH_COVERAGE = "it_branch_coverage";
  String IT_BRANCH_COVERAGE_HITS_DATA = "it_branch_coverage_hits_data";
}
