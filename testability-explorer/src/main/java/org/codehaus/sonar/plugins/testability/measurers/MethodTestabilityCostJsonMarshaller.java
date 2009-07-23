package org.codehaus.sonar.plugins.testability.measurers;

import org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDetail;
/**
 * Marshalls a org.codehaus.sonar.plugins.testability.client.model.MethodTestabilityCostDetail
 * to a Json Member. For instance: <pre>{"cyclomatic":12,"global":400,"lod":10,"overall":400}</pre>
 * 
 * @author cedric.lamalle
 *
 */
public class MethodTestabilityCostJsonMarshaller extends AbstractJsonMarshaller<MethodTestabilityCostDetail> {
  
  private static final String OVERALL = "overall";
  private static final String LOD = "lod";
  private static final String CYCLOMATIC = "cyclomatic";
  private static final String GLOBAL = "global";
  private static final int JSON_OBJECT_SIZE = 50;
  
  public String marshall(MethodTestabilityCostDetail detail) {
    StringBuilder stringBuilder = new StringBuilder(JSON_OBJECT_SIZE);
    stringBuilder.append("{");
    writeMemberAndComma(stringBuilder, CYCLOMATIC, detail.getCyclomaticComplexity());
    writeMemberAndComma(stringBuilder, GLOBAL, detail.getGlobal());
    writeMemberAndComma(stringBuilder, LOD, detail.getLawOfDemeter());
    writeMember(stringBuilder, OVERALL, detail.getOverall());
    stringBuilder.append("}");
    return stringBuilder.toString();
  }

  public MethodTestabilityCostDetail unMarshall(String marshalledDetail) {
    // TODO Auto-generated method stub
    return null;
  }

}
