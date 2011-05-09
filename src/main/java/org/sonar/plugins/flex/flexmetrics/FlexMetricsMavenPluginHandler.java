/*
 * Sonar Flex Plugin
 * Copyright (C) 2010 SonarSource
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

package org.sonar.plugins.flex.flexmetrics;

import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.resources.Project;

public class FlexMetricsMavenPluginHandler implements MavenPluginHandler {
  public String getGroupId() {
    return "com.adobe.ac";
  }

  public String getArtifactId() {
    return "flex-pmd-metrics-maven-plugin";
  }

  public String getVersion() {
    return "1.2";
  }

  public boolean isFixedVersion() {
    return true;
  }

  public String[] getGoals() {
    return new String[] { "check" };
  }

  public void configure(Project project, MavenPlugin plugin) {
  }
}
