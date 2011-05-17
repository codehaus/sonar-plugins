/**
 * 
 */
package com.echosource.ada.gnat.metric.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @author Akram Ben Aissi
 * 
 */
@XStreamAlias("global")
public class GlobalNode {

  /**
   * Files on which we have metrics.
   */
  @XStreamImplicit(itemFieldName = "metric")
  @XStreamConverter(MetricNodeConverter.class)
  private List<MetricNode> metrics;

  /**
   * Files on which we have metrics.
   */
  @XStreamImplicit(itemFieldName = "file")
  private List<FileNode> files;

  /**
   * @return the files
   */
  public List<FileNode> getFiles() {
    return files;
  }

  /**
   * @return the metrics
   */
  public List<MetricNode> getMetrics() {
    return metrics;
  }

}
