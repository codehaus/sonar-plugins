package org.codehaus.sonar.plugins.testability.measurers;

import org.codehaus.sonar.plugins.testability.client.model.ViolationCostDetail;

public class ViolationCostDetailJsonMarshaller extends AbstractJsonMarshaller<ViolationCostDetail> {
  
  private static final String LOD = "lod";
  private static final String CYCLOMATIC = "cyclomatic";
  private static final String GLOBAL = "global";
  private static final String REASON = "reason";
  private static final String OVERALL = "overall";
  
  public String marshall(ViolationCostDetail detail) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("{");
    writeMemberAndComma(stringBuilder, CYCLOMATIC, detail.getCyclomaticComplexity());
    writeMemberAndComma(stringBuilder, GLOBAL, detail.getGlobal());
    writeMemberAndComma(stringBuilder, LOD, detail.getLawOfDemeter());
    writeMemberAndComma(stringBuilder, OVERALL, detail.getOverall());
    writeMember(stringBuilder, REASON, detail.getReason(), true);
    stringBuilder.append("}");
    return stringBuilder.toString();
  }

  public ViolationCostDetail unMarshall(String marshalledDetail) {
    // TODO Auto-generated method stub
    return null;
  }

}
