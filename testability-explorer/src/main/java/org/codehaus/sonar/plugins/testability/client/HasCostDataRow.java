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

package org.codehaus.sonar.plugins.testability.client;

import org.codehaus.sonar.plugins.testability.client.model.HasCostData;
import org.sonar.api.web.gwt.client.widgets.AbstractSourcePanel.Row;

public class HasCostDataRow extends Row {

  public HasCostDataRow(HasCostData hasCostData, int lineIndex, String source) {
    super(lineIndex, source);
    if (isShowingComplexity(hasCostData)) {
      setColumn2(Integer.toString(hasCostData.getLawOfDemeter()));
      setColumn3(Integer.toString(hasCostData.getCyclomaticComplexity()));
    }
  }

  private boolean isShowingComplexity(HasCostData hasCostData) {
    return hasCostData.getCyclomaticComplexity() != 0;
  }
}
