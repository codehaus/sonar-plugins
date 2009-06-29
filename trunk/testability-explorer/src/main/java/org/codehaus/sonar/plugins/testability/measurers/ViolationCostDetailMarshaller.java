package org.codehaus.sonar.plugins.testability.measurers;

import java.util.HashMap;
import java.util.Map;

import org.sonar.plugins.api.measures.KeyValueFormat;

public class ViolationCostDetailMarshaller implements CostMarshaller<ViolationCostDetail> {

  private static final String LOD = "lod";
  private static final String CYCLOMATIC = "cyclomatic";
  private static final String GLOBAL = "global";
  private static final String REASON = "reason";
  private static final Object OVERALL = "overall";
  
  public String marshall(ViolationCostDetail detail) {
    HashMap<Object, Object> map = new HashMap<Object, Object>();
    map.put(LOD, detail.getLawOfDemeter());
    map.put(CYCLOMATIC, detail.getCyclomaticComplexity());
    map.put(GLOBAL, detail.getGlobal());
    map.put(REASON, detail.getReason());    
    map.put(OVERALL, detail.getOverall());
    return KeyValueFormat.format(map);
  }

  public ViolationCostDetail unMarshall(String marshalledDetail) {
    Map<String, String> map = KeyValueFormat.parse(marshalledDetail);
    ViolationCostDetail violationCostDetail = new ViolationCostDetail();
    violationCostDetail.setCyclomaticComplexity(Integer.valueOf(map.get(CYCLOMATIC)));
    violationCostDetail.setLawOfDemeter(Integer.valueOf(map.get(LOD)));
    violationCostDetail.setGlobal(Integer.valueOf(map.get(GLOBAL)));
    violationCostDetail.setReason(map.get(REASON));
    violationCostDetail.setOverall(Integer.valueOf(map.get(OVERALL)));
    return violationCostDetail;
  }

}
