/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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
package org.sonar.plugins.ral.batch;

import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.batch.maven.MavenUtils;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.resources.Project;

public class TodoMavenPluginHandler implements MavenPluginHandler {
  
  public String getGroupId() {
    return MavenUtils.GROUP_ID_CODEHAUS_MOJO;
  }

  public String getArtifactId() {
    return "taglist-maven-plugin";
  }

  public String getVersion() {
    return "2.2";
  }

  public boolean isFixedVersion() {
    return false;
  }

  public String[] getGoals() {
    return new String[]{"taglist"};
  }

  public void configure(Project project, MavenPlugin plugin) {
    plugin.removeParameter("parameterToRemove");
    plugin.setParameter("parameterToSet", "true");
  }
}
