/*
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

package org.codehaus.sonar.plugins.testability.client.webservices;

import org.sonar.api.web.gwt.client.webservices.WSMetrics.Metric;

public final class WSTestabilityMetrics {
  public static final Metric TESTABILITY_COST = new Metric("testability_cost");
  public static final Metric METHOD_DETAILS_COST = new Metric("testability_method_details");

  private WSTestabilityMetrics() {
    // Constant Class
  }
}
