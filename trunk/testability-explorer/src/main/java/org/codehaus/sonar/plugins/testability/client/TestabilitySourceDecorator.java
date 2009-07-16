package org.codehaus.sonar.plugins.testability.client;

import java.util.Arrays;

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

public class TestabilitySourceDecorator extends SourceDecorator<Resources> {

  private final class TestabilityDecoratorCallBack extends DecoratorCallBack<Resources> {
    private TestabilityDecoratorCallBack(SourcePanel sourcePanel) {
      super(sourcePanel);
    }

    @Override
    public void initializeDecorator(Resources response, JavaScriptObject jsonRawResponse) {
      if (responseHasRightMeasure(response)) {
        Measure measure = getMethodCostMeasure(response);
        measure.getValue();
      }
    }

    private Measure getMethodCostMeasure(Resources response) {
      return response.getResources().get(0).getMeasures().get(WSTestabilityMetrics.METHOD_DETAILS_COST);
    }

    private boolean responseHasRightMeasure(Resources response) {
      return response.getResources().size() == 1 && response.getResources().get(0).hasMeasure(WSTestabilityMetrics.METHOD_DETAILS_COST);
    }
  }

  public TestabilitySourceDecorator(Resource resource) {
    super(resource);
  }

  @Override
  protected Query<Resources> getDecorationQuery() {
    return ResourcesQuery.get(getResource().getKey()).setMetrics(Arrays.asList(WSTestabilityMetrics.METHOD_DETAILS_COST));
  }

  @Override
  protected DecoratorCallBack<Resources> getQueryCallBack(SourcePanel sourcePanel) {
    return new TestabilityDecoratorCallBack(sourcePanel);
  }
}
