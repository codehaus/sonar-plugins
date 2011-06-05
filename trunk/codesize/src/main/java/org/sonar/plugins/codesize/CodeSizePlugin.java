/*
 * Sonar Codesize Plugin
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
package org.sonar.plugins.codesize;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

/**
 * Codesize Plugin calculates lines of code for all languages in a project.
 *
 * @author Matthijs Galesloot
 * @since 1.0
 */
@Properties({
    @Property(
        key = CodesizeConstants.SONAR_CODESIZE_PROFILE,
        name = "Profile",
        description = "Codesize profile. Define per group of files the include pattern and " +
          "optionally an exclude pattern<br><br>Example: <br>Java<br>includes = src/main/java/**/*.java<br>excludes = src/main/**/*generated.java",
        global = true, project = true),
    @Property(key = CodesizeConstants.SONAR_CODESIZE_ACTIVE, name = "Active", description = "Should code size plugin be running.",
        defaultValue = "true", global = true, project = true) })
public class CodeSizePlugin implements Plugin {

  public String getDescription() {
    return null;
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();

    list.add(LineCountSensor.class);
    list.add(CodesizeMetrics.class);
    list.add(CodeSizeDecorator.class);
    list.add(DashboardWidget.class);

    return list;
  }

  public String getKey() {
    return null;
  }

  public String getName() {
    return null;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
