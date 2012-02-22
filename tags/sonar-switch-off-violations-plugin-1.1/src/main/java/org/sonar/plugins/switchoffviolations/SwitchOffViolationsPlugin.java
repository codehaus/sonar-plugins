/*
 * Sonar Switch Off Violations Plugin
 * Copyright (C) 2011 SonarSource
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

package org.sonar.plugins.switchoffviolations;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

@Properties({
  @Property(
    key = Constants.PATTERNS_PARAMETER_KEY,
    defaultValue = "",
    name = "Exclusion patterns",
    description = "Patterns used to identify which violations to switch off.<br/>Each pattern must be defined on a new line. "
      +
      "Comments start with #. Blank lines are allowed. A line defines 3 fields: resource key, rule key and range of lines.<br/><br/>"
      +
      "Example:<br/><pre># exclude a specific rule on a specific file on specific lines\ncom.foo.Bar;pmd:AvoidPrintStackTrace;[10,25,90]</pre>"
      +
      "<br/>More information on the <a href=\"http://docs.codehaus.org/display/SONAR/Switch+Off+Violations+Plugin\">documentation page of the plugin</a>.<br/><br/>",
    project = true,
    global = true),
  @Property(
      key = Constants.LOCATION_PARAMETER_KEY,
      defaultValue = "",
      name = "Configuration file for exclusion patterns",
      description = "Location of a file that would contain the exclusion patterns and that would be store along with the source code.",
      project = true,
      global = true)
})
public final class SwitchOffViolationsPlugin implements Plugin {
  public String getKey() {
    return Constants.PLUGIN_KEY;
  }

  public String getName() {
    return Constants.PLUGIN_NAME;
  }

  public String getDescription() {
    return "";
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public List getExtensions() {
    return Arrays.asList(SwitchOffViolationsFilter.class);
  }
}
