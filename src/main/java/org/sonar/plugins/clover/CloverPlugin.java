/*
 * Sonar Clover Plugin
 * Copyright (C) 2008 SonarSource
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

package org.sonar.plugins.clover;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;

import java.util.Arrays;
import java.util.List;

@Properties({
  @Property(
    key = CloverConstants.LICENSE_PROPERTY,
    name = "License",
    description = "You can obtain a free 30 day evaluation license or purchase a commercial license at <a href='http://my.atlassian.com'>http://my.atlassian.com</a>.",
    project = true, global = true),
  @Property(
    key = CloverConstants.REPORT_PATH_PROPERTY,
    name = "Report path",
    description = "Absolute or relative path to XML report file.",
    project = true, global = true),
  @Property(
    key = CloverConstants.VERSION_PROPERTY,
    name = "Clover version",
    description = "Override the Clover version to use. Default value is read from pom, else " + CloverConstants.MAVEN_DEFAULT_VERSION,
    project = true, global = true, defaultValue = CloverConstants.MAVEN_DEFAULT_VERSION)})
public final class CloverPlugin extends SonarPlugin {

  public List getExtensions() {
    return Arrays.asList(CloverSettings.class, CloverMavenPluginHandler.class, CloverMavenInitializer.class, CloverSensor.class);
  }
}
