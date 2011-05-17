package com.echosource.ada.gnat.metric.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * TODO Migrate to JAXB ?
 * 
 * @author Akram Ben Aissi
 */
public class MetricNodeConverter implements Converter {

  public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
    MetricNode metricNode = (MetricNode) value;
    writer.addAttribute("name", metricNode.getName());
    writer.setValue(metricNode.getValue().toString());
  }

  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    MetricNode metricNode = new MetricNode();
    // The order is important?
    metricNode.setName(reader.getAttribute("name"));
    metricNode.setValue(Double.valueOf(reader.getValue()));
    return metricNode;
  }

  public boolean canConvert(Class clazz) {
    return clazz == MetricNode.class;
  }
}
