/**
 * 
 */
package org.codehaus.sonar.plugins.testability.client;

import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDataDecoder;
import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDataDecoderImpl;
import org.codehaus.sonar.plugins.testability.client.webservices.WSTestabilityMetrics;
import org.sonar.plugins.api.web.gwt.client.SourcePanel;
import org.sonar.plugins.api.web.gwt.client.SourcePanel.DecoratorCallBack;
import org.sonar.plugins.api.web.gwt.client.webservices.Measure;
import org.sonar.plugins.api.web.gwt.client.webservices.Resources;

import com.google.gwt.core.client.JavaScriptObject;

final class TestabilityDecoratorCallBack extends DecoratorCallBack<Resources> {
  private MethodTestabilityCostDataDecoder decoder;
  private TestabilitySourceDecorator testabilitySourceDecorator;
  
  TestabilityDecoratorCallBack(SourcePanel sourcePanel, TestabilitySourceDecorator testabilitySourceDecorator) {
    super(sourcePanel);
    this.testabilitySourceDecorator = testabilitySourceDecorator;
  }
  
  public MethodTestabilityCostDataDecoder getDecoder() {
    if (this.decoder == null) {
      this.decoder = new MethodTestabilityCostDataDecoderImpl();
    }

    return this.decoder;
  }

  public void setDecoder(MethodTestabilityCostDataDecoder decoder) {
    this.decoder = decoder;
  }

  @Override
  public void initializeDecorator(Resources response, JavaScriptObject jsonRawResponse) {
    if (responseHasRightMeasure(response)) {
      Measure measure = getMethodCostMeasure(response);
      testabilitySourceDecorator.setMethodTestabilityCostData(getDecoder().decode(measure.getValue()));
    }
  }

  private Measure getMethodCostMeasure(Resources response) {
    return response.getResources().get(0).getMeasures().get(WSTestabilityMetrics.METHOD_DETAILS_COST);
  }

  private boolean responseHasRightMeasure(Resources response) {
    return response.getResources().size() == 1 && response.getResources().get(0).hasMeasure(WSTestabilityMetrics.METHOD_DETAILS_COST);
  }
}