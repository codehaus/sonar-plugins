/*
 * Ada Sonar Plugin
 * Copyright (C) 2010 Akram Ben Aissi
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.ada.gnat.metric.xml;

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

  @SuppressWarnings("unchecked")
  public boolean canConvert(Class clazz) {
    return clazz == MetricNode.class;
  }
}
