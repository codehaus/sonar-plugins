/*
 * Codesize
 * Copyright (C) 2010 Matthijs Galesloot
 * dev@sonar.codehaus.org
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

import java.util.ArrayList;
import java.util.List;

import org.sonar.plugins.codesize.SizingMetrics;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("profile")
public class SizingProfile {

  @XStreamImplicit
  private List<SizingMetric> sizingMetrics = new ArrayList<SizingMetric>();

  public List<SizingMetric> getSizingMetrics() {
    return sizingMetrics;
  }

  public void setSizingMetrics(List<SizingMetric> metrics) {
    this.sizingMetrics = metrics;
  }

  private static XStream getXStream() {
    XStream xstream = new XStream();
    xstream.setClassLoader(SizingMetrics.class.getClassLoader());
    xstream.processAnnotations(SizingProfile.class);
    xstream.processAnnotations(SizingMetric.class);

    return xstream;
  }

  public static SizingProfile fromXML(String codeSizeProfile) {
    return (SizingProfile) getXStream().fromXML(codeSizeProfile);
  }
}