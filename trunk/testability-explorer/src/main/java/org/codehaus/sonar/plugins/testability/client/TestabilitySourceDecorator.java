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
