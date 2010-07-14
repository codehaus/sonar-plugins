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

package org.codehaus.sonar.plugins.testability;

import org.codehaus.sonar.plugins.testability.client.GwtTestabilityDetailsViewer;
import org.codehaus.sonar.plugins.testability.client.webservices.WSTestabilityMetrics;
import org.sonar.api.web.*;
import org.sonar.wsclient.services.Resource;

@ResourceQualifier({Resource.QUALIFIER_CLASS})
@ResourceScope({Resource.SCOPE_ENTITY})
@NavigationSection(NavigationSection.RESOURCE_TAB)
@DefaultTab(metrics = {
    WSTestabilityMetrics.TESTABILITY_COST,
    WSTestabilityMetrics.METHOD_DETAILS_COST
})
public class TestabilityDetailsViewer extends GwtPage {

  public String getTitle() {
    return "Testability Explorer Details";
  }

  public String getGwtId() {
    return GwtTestabilityDetailsViewer.GWT_ID;
  }

}
