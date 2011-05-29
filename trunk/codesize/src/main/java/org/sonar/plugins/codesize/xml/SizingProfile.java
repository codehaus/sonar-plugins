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

import org.apache.commons.lang.StringUtils;

public class SizingProfile {

  private List<SizingMetric> sizingMetrics = new ArrayList<SizingMetric>();

  public List<SizingMetric> getSizingMetrics() {
    return sizingMetrics;
  }

  public void setSizingMetrics(List<SizingMetric> metrics) {
    this.sizingMetrics = metrics;
  }

  private static final String INCLUDES = "includes";
  private static final String EXCLUDES = "includes";

  public void parse(String codeSizeProfile) {
    String[] lines = StringUtils.split(codeSizeProfile, "\n");
    SizingMetric metric = null;
    for (String line : lines) {
      if ( !StringUtils.isBlank(line)) {
        String[] kv = line.split("[:=]");
        if (kv.length > 1) {
          if (INCLUDES.contains(kv[0])) {
            metric.addIncludes(kv[1].trim());
          } else if (EXCLUDES.contains(kv[0])) {
            metric.addExcludes(kv[1].trim());
          }
        } else {
          metric = new SizingMetric();
          metric.setName(line.trim());
          getSizingMetrics().add(metric);
        }
      }
    }
  }
}