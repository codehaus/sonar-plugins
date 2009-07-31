package org.codehaus.sonar.plugins.testability.client;

import java.util.Arrays;
import java.util.List;

import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostData;
import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDetail;
import org.codehaus.sonar.plugins.testability.client.model.ViolationCostDetail;
import org.codehaus.sonar.plugins.testability.client.webservices.WSTestabilityMetrics;
import org.sonar.plugins.api.web.gwt.client.SourceDecorator;
import org.sonar.plugins.api.web.gwt.client.SourcePanel;
import org.sonar.plugins.api.web.gwt.client.SourcePanel.DecoratorCallBack;
import org.sonar.plugins.api.web.gwt.client.webservices.Query;
import org.sonar.plugins.api.web.gwt.client.webservices.Resource;
import org.sonar.plugins.api.web.gwt.client.webservices.Resources;
import org.sonar.plugins.api.web.gwt.client.webservices.ResourcesQuery;


public class TestabilitySourceDecorator extends SourceDecorator<Resources> {

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
    return new TestabilityDecoratorCallBack(sourcePanel, this);
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
    List<ViolationCostDetail> violationsOfLine = getCostData().getViolationsOfLine(lineIndex);
    return (violationsOfLine != null && violationsOfLine.size() > 0) || getCostData().getMethodCostOfLine(lineIndex) != null;
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
        html += "<li class='error'><h3>" + violationCostDetail.getReason() + "</h3>";
        html += " Cyclomatic:" + violationCostDetail.getCyclomaticComplexity();
        html += " Global:" + violationCostDetail.getGlobal();
        html += " Law of Demeter:" + violationCostDetail.getLawOfDemeter();
        html += " Overall:" + violationCostDetail.getOverall() + "</li>";
      }
      html += "</ul></div>";
    }
    return html;
  }

  private String getMethodCostMessage(int lineIndex) {
    MethodTestabilityCostDetail methodCost = getCostData().getMethodCostOfLine(lineIndex);
    String html = "";
    if (methodCost != null) {
      html = "<div class='msg'><ul>";
      html += "<li class='error'> Cyclomatic:" + methodCost.getCyclomaticComplexity();
      html += " Global:" + methodCost.getGlobal();
      html += " Law of Demeter:" + methodCost.getLawOfDemeter();
      html += " Overall:" + methodCost.getOverall() + "</li>";
      html += "</ul></div>";
    }
    return html;
  }

  public void setMethodTestabilityCostData(MethodTestabilityCostData costData) {
    this.costData = costData;
  }

}
