/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.emma;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;

import java.util.Arrays;
import java.util.List;

@Properties({
  @Property(
    key = EmmaPlugin.REPORT_PATH_PROPERTY,
    name = "Report file",
    description = "Path (absolute or relative) of directory where the .ec and the .em Emma files are generated. Do not set value when using default maven settings.",
    module = true,
    project = true,
    global = false
  )
})
public class EmmaPlugin extends SonarPlugin {

  public static final String REPORT_PATH_PROPERTY = "sonar.emma.reportPath";
  public static final String META_DATA_SUFFIX = ".em";
  public static final String COVERAGE_DATA_SUFFIX = ".ec";
  public static final String JAVA_LANGUAGE_KEY = "java";
  public static final String PLUGIN_KEY = "emma";

  public List getExtensions() {
    return Arrays.asList(EmmaSettings.class, EmmaMavenInitializer.class, EmmaMavenPluginHandler.class, EmmaSensor.class);
  }

}
