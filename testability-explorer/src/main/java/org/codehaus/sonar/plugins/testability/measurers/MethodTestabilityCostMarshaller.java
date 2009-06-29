package org.codehaus.sonar.plugins.testability.measurers;

import java.util.HashMap;
import java.util.Map;

import org.sonar.plugins.api.measures.KeyValueFormat;

public class MethodTestabilityCostMarshaller implements CostMarshaller<MethodTestabilityCostDetail> {

  private static final String OVERALL = "overall";
  private static final String LOD = "lod";
  private static final String CYCLOMATIC = "cyclomatic";
  private static final String GLOBAL = "global";

  public String marshall(MethodTestabilityCostDetail detail) {
    HashMap<Object, Object> map = new HashMap<Object, Object>();
    map.put(GLOBAL, detail.getGlobal());
    map.put(CYCLOMATIC, detail.getCyclomaticComplexity());
    map.put(LOD, detail.getLawOfDemeter());
    map.put(OVERALL, detail.getOverall());
    return KeyValueFormat.format(map);
  }

  public MethodTestabilityCostDetail unMarshall(String marshalledDetail) {
    Map<String, String> map = KeyValueFormat.parse(marshalledDetail);
    MethodTestabilityCostDetail method = new MethodTestabilityCostDetail();
    method.setGlobal(Integer.valueOf(map.get(GLOBAL)));
    method.setCyclomaticComplexity(Integer.valueOf(map.get(CYCLOMATIC)));
    method.setLawOfDemeter(Integer.valueOf(map.get(LOD)));
    method.setOverall(Integer.valueOf(map.get(OVERALL)));
    return method;
  }
  
}
