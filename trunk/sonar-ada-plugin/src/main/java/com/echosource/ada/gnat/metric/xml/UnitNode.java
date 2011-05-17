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
@XStreamAlias("unit")
public class UnitNode {

  /**
   * Unit name.
   */
  @XStreamAsAttribute
  private String name;
  /**
   * Unit kind.
   */
  @XStreamAsAttribute
  private String kind;

  private UnitNode unit;

  /**
   * Line on which the unit is located.
   */
  private Double line;
  /**
   * Column on which unit is located.
   */
  private Double column;
  /**
   * Metrics related to the unit.
   */
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
   * @return the kind
   */
  public String getKind() {
    return kind;
  }

  /**
   * @return the line
   */
  public Double getLine() {
    return line;
  }

  /**
   * @return the column
   */
  public Double getColumn() {
    return column;
  }

  /**
   * @return the metrics
   */
  public List<MetricNode> getMetrics() {
    return metrics;
  }

  /**
   * @return the unit
   */
  public UnitNode getUnit() {
    return unit;
  }
}
