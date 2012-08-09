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

public class RequestConstants {

  private static final String CONDITION_REQUEST = "FROM ResourceModel p, Snapshot s, MeasureModel pm "
    + "WHERE pm.metricId = :metricId AND pm.snapshotId = s.id AND s.resourceId = p.id "
    + "AND s.last = :last AND p.rootId is null and p.languageKey is not null "
    + "GROUP BY p.languageKey ORDER BY p.languageKey";

  public static final String METRIC_AVERAGE_BY_LANGUAGE = "SELECT p.languageKey, avg(pm.value), min(pm.value), "
    + "max(pm.value), max(s.createdAt) " + CONDITION_REQUEST;

  public static final String NB_LOC_AND_PROJECT_BY_LANGUAGE = "SELECT p.languageKey, sum(pm.value), "
    + "count(p.id), max(s.createdAt) " + CONDITION_REQUEST;

  private RequestConstants() {
    // Don't instantiate this Singleton
  }

}
