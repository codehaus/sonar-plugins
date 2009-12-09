package org.codehaus.sonar.plugins.testability;

import org.codehaus.sonar.plugins.testability.client.GwtTestabilityDetailsViewer;
import org.sonar.api.web.ResourceViewer;

public class TestabilityDetailsViewer implements ResourceViewer {

  public String getTitle() {
    return "Testability Explorer Details";
  }

  public String getGwtId() {
    return GwtTestabilityDetailsViewer.GWT_ID;
  }

}
