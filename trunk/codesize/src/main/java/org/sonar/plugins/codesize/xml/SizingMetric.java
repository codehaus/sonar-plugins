/*
 * Copyright (C) 2010 The Original Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonar.plugins.codesize.xml;

import org.sonar.api.measures.Metric;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("metric")
public class SizingMetric implements Comparable<String> {

  @XStreamAsAttribute
  private String description;

  @XStreamAsAttribute
  private String key;

  private Metric metric;

  @XStreamAsAttribute
  private String name;

  @XStreamAsAttribute
  private String sourceDir;

  @XStreamAsAttribute
  private String suffix;

  public int compareTo(String o) {
    return o.compareTo(key);
  }

  public String getDescription() {
    return description;
  }

  public String getKey() {
    return key;
  }

  public Metric getMetric() {
    return metric;
  }

  public String getName() {
    return name;
  }

  public String getSourceDir() {
    return sourceDir;
  }

  public String getSuffix() {
    return suffix;
  }

  public void setMetric(Metric metric) {
    this.metric = metric;
  }
}