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

package org.sonar.plugins.greenpepper;

import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.resources.Project;

public class GreenPepperMavenPluginHandler implements MavenPluginHandler {

  public static final String GROUP_ID = "greenpepper";
  public static final String ARTIFACT_ID = "greenpepper-maven-plugin";

  public String getGroupId() {
    return GROUP_ID;
  }

  public String getArtifactId() {
    return ARTIFACT_ID;
  }

  public String getVersion() {
    return "2.0";
  }

  public boolean isFixedVersion() {
    return false;
  }

  public String[] getGoals() {
    return new String[]{"run"};
  }

  public boolean dependsUponCustomRules() {
    return false;
  }


  public void configure(Project project, MavenPlugin mavenPlugin) {
  }
}
