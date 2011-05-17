/**
 * 
 */
package com.echosource.ada.gnat.metric.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author Akram Ben Aissi
 * 
 */
@XStreamAlias("metric")
public class MetricNode {

  /**
   * Metric name.
   */
  @XStreamAlias("name")
  @XStreamAsAttribute
  private String name;
  /**
   * Value of the metric.
   */
  @XStreamAlias("metric")
  private Double value;

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the value
   */
  public Double getValue() {
    return value;
  }

}
