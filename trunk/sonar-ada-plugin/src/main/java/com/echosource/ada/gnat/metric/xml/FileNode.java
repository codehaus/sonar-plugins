/**
 * 
 */
package com.echosource.ada.gnat.metric.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
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
  private List<MetricNode> metrics;

  private List<UnitNode> units;

  private CouplingNode coupling;

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
   * @return the units
   */
  public List<UnitNode> getUnits() {
    return units;
  }

  /**
   * @return the coupling
   */
  public CouplingNode getCoupling() {
    return coupling;
  }

}
