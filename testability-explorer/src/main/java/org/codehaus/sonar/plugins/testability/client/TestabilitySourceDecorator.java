package org.codehaus.sonar.plugins.testability.client;



public class TestabilitySourceDecorator /*extends SourceDecorator<Resources>*/ {
/*


  @Override
  public String decorateValue(int lineIndex) {
    String cssClass = "green";
    if (hasCost(lineIndex)) {
      cssClass = "red";
    }
    return "<div class='" + CSS_CLASS_VALUE + " " + cssClass + "'>" + lineIndex + "</div>";
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
    HasCostData methodCost = getCostData().getMethodCostOfLine(lineIndex);
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
*/
}
