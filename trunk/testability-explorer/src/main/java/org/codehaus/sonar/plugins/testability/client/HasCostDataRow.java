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
