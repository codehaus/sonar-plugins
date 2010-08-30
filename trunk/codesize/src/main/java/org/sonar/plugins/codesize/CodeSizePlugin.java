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

package org.sonar.plugins.codesize;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.plugins.codesize.dashboard.DashboardWidget;

public class CodeSizePlugin implements Plugin {

  private static final String KEY = "sonar-codesize-plugin";

  public String getKey() {
    return KEY;
  }

  public String getName() {
    return "Codesize plugin";
  }

  public String getDescription() {
    return "Provides metrics about code size";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();

    list.add(LineCountSensor.class);
    list.add(SizingMetrics.class);
    list.add(SummaryMetrics.class);
    list.add(CodeSizeDecorator.class);
    list.add(DashboardWidget.class);

    return list;
  }

  @Override
  public String toString() {
    return getKey();
  }
}
