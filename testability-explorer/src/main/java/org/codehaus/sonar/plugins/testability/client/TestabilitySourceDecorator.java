package org.codehaus.sonar.plugins.testability.client;

import java.util.Arrays;

import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostData;
import org.codehaus.sonar.plugins.testability.client.webservices.WSTestabilityMetrics;
import org.sonar.plugins.api.web.gwt.client.SourceDecorator;
import org.sonar.plugins.api.web.gwt.client.SourcePanel;
import org.sonar.plugins.api.web.gwt.client.SourcePanel.DecoratorCallBack;
import org.sonar.plugins.api.web.gwt.client.webservices.Measure;
import org.sonar.plugins.api.web.gwt.client.webservices.Query;
import org.sonar.plugins.api.web.gwt.client.webservices.Resource;
import org.sonar.plugins.api.web.gwt.client.webservices.Resources;
import org.sonar.plugins.api.web.gwt.client.webservices.ResourcesQuery;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class TestabilitySourceDecorator extends SourceDecorator<Resources> {

  private final class TestabilityDecoratorCallBack extends DecoratorCallBack<Resources> {
    private TestabilityDecoratorCallBack(SourcePanel sourcePanel) {
      super(sourcePanel);
    }

    @Override
    public void initializeDecorator(Resources response, JavaScriptObject jsonRawResponse) {
      if (responseHasRightMeasure(response)) {
        Measure measure = getMethodCostMeasure(response);
        JSONValue value = JSONParser.parse(measure.getValue());
        value.isObject();
      }
    }

    private Measure getMethodCostMeasure(Resources response) {
      return response.getResources().get(0).getMeasures().get(WSTestabilityMetrics.METHOD_DETAILS_COST);
    }

    private boolean responseHasRightMeasure(Resources response) {
      return response.getResources().size() == 1 && response.getResources().get(0).hasMeasure(WSTestabilityMetrics.METHOD_DETAILS_COST);
    }
  }

  private MethodTestabilityCostData costData;

  public TestabilitySourceDecorator(Resource resource) {
    super(resource);
  }

  protected MethodTestabilityCostData getCostData() {
    if (this.costData == null) {
      this.costData = new MethodTestabilityCostData();
    }
    return this.costData;
  }

  @Override
  protected Query<Resources> getDecorationQuery() {
    return ResourcesQuery.get(getResource().getKey()).setMetrics(Arrays.asList(WSTestabilityMetrics.METHOD_DETAILS_COST));
  }

  @Override
  protected DecoratorCallBack<Resources> getQueryCallBack(SourcePanel sourcePanel) {
    return new TestabilityDecoratorCallBack(sourcePanel);
  }

  @Override
  public String decorateValue(int lineIndex) {
    // TODO Auto-generated method stub
    return super.decorateValue(lineIndex);
  }

  @Override
  public String decorateSource(int lineIndex, String source) {
    // TODO Auto-generated method stub
    return super.decorateSource(lineIndex, source);
  }

}
