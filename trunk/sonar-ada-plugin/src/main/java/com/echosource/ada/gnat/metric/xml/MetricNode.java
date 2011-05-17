/**
 * 
 */
package com.echosource.ada.gnat.metric.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * @author Akram Ben Aissi
 * 
 */
@XStreamAlias("metric")
@XStreamConverter(MetricNodeConverter.class)
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

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @param value
   *          the value to set
   */
  public void setValue(Double value) {
    this.value = value;
  }

}
