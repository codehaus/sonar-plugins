package org.codehaus.sonar.plugins.testability.client;

import java.util.Arrays;
import java.util.List;

import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostData;
import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDataDecoder;
import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDataDecoderImpl;
import org.codehaus.sonar.plugins.testability.client.model.ViolationCostDetail;
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
    private MethodTestabilityCostDataDecoder decoder;

    public MethodTestabilityCostDataDecoder getDecoder() {
      if (this.decoder == null) {
        this.decoder = new MethodTestabilityCostDataDecoderImpl();

      }

      return this.decoder;
    }

    public void setDecoder(MethodTestabilityCostDataDecoder decoder) {
      this.decoder = decoder;
    }

    private TestabilityDecoratorCallBack(SourcePanel sourcePanel) {
      super(sourcePanel);
    }

    @Override
    public void initializeDecorator(Resources response, JavaScriptObject jsonRawResponse) {
      if (responseHasRightMeasure(response)) {
        Measure measure = getMethodCostMeasure(response);
        setMethodTestabilityCostData(getDecoder().decode(measure.getValue()));
      }
    }

    private MethodTestabilityCostData methodTestabilityCostData;

    public MethodTestabilityCostData getMethodTestabilityCostData() {
      return this.methodTestabilityCostData;
    }

    public void setMethodTestabilityCostData(MethodTestabilityCostData methodTestabilityCostData) {
      this.methodTestabilityCostData = methodTestabilityCostData;
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
    String cssClass = "green";
    if (hasCost(lineIndex)) {
      cssClass = "red";
    }
    return "<div class='" + CSS_CLASS_VALUE + " " + cssClass + "'>" + lineIndex + "</div>";
  }

  private boolean hasCost(int lineIndex) {
    return getCostData().getViolationsOfLine(lineIndex) != null || getCostData().getMethodCostOfLine(lineIndex) != null;
  }

  @Override
  public String decorateSource(int lineIndex, String source) {
    if (!hasCost(lineIndex)) {
      return "<div class='" + CSS_CLASS_SOURCE + "'><pre>" + source + "</pre></div>";
    }
    return "<div class='" + CSS_CLASS_SOURCE + " red" + "'><pre>" + source + "</pre>" + getMethodCostMessage(lineIndex) + " "
        + getViolationCostMessage(lineIndex) + "</div>";
  }

  private String getViolationCostMessage(int lineIndex) {
    List<ViolationCostDetail> violationsOfLine = getCostData().getViolationsOfLine(lineIndex);
    String html = "";
    if (violationsOfLine.size() > 0) {
      html = "<div class='msg'><ul>";
      for (ViolationCostDetail violationCostDetail : violationsOfLine) {
        //
      }
      html += "</ul></div>";
    }
    return html;
  }

  private String getMethodCostMessage(int lineIndex) {
    // TODO Auto-generated method stub
    return "";
  }

}
