/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SonarSource
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

package org.sonar.plugins.jacoco;

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import java.util.Arrays;
import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
@Properties({
    @Property(
        key = JaCoCoPlugin.REPORT_PATH_PROPERTY,
        name = "File with execution data",
        defaultValue = JaCoCoPlugin.REPORT_PATH_DEFAULT_VALUE,
        description = "Path (absolute or relative) to the file with execution data.",
        global = false,
        module = true,
        project = true
    ),
    @Property(
        key = JaCoCoPlugin.INCLUDES_PROPERTY,
        name = "Includes",
        description = "A list of class names that should be included in execution analysis." +
            " The list entries are separated by a vertical bar (|) and may use wildcard characters (* and ?)." +
            " Except for performance optimization or technical corner cases this option is normally not required.",
        global = false,
        project = true,
        module = true
    ),
    @Property(
        key = JaCoCoPlugin.EXCLUDES_PROPERTY,
        name = "Excludes",
        description = "A list of class names that should be excluded from execution analysis." +
            " The list entries are separated by a vertical bar (|) and may use wildcard characters (* and ?)." +
            " Except for performance optimization or technical corner cases this option is normally not required.",
        global = false,
        project = true,
        module = true
    )
})
public class JaCoCoPlugin implements Plugin {

  public static final String REPORT_PATH_PROPERTY = "sonar.jacoco.reportPath";
  public static final String REPORT_PATH_DEFAULT_VALUE = "target/jacoco.exec";
  public static final String INCLUDES_PROPERTY = "sonar.jacoco.includes";
  public static final String EXCLUDES_PROPERTY = "sonar.jacoco.excludes";

  public String getKey() {
    return "jacoco";
  }

  public String getName() {
    return "JaCoCo";
  }

  public String getDescription() {
    return "<a href='http://www.eclemma.org/jacoco/'>JaCoCo</a> calculates coverage of unit tests." +
        " Set the parameter 'Code coverage plugin' to <code>jacoco</code> in the General plugin.";
  }

  public List getExtensions() {
    return Arrays.asList(SurefireMavenPluginHandler.class, JaCoCoSensor.class);
  }

  @Override
  public String toString() {
    return getKey();
  }

}
