/**
 * 
 */
package com.echosource.ada.gnat.metric.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @author Akram Ben Aissi
 * 
 */
@XStreamAlias("file")
public class FileNode {

  @XStreamAsAttribute
  private String name;

  @XStreamImplicit
  private List<UnitNode> units;

  private CouplingNode coupling;

  @XStreamImplicit
  @XStreamConverter(MetricNodeConverter.class)
  private List<MetricNode> metrics;

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the metrics
   */
  public List<MetricNode> getMetrics() {
    return metrics;
  }

  /**
   * @return the coupling
   */
  public CouplingNode getCoupling() {
    return coupling;
  }

  /**
   * @return the units
   */
  public List<UnitNode> getUnits() {
    return units;
  }

}
