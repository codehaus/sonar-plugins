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
package org.sonar.plugins.emma;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.batch.maven.MavenUtils;
import org.sonar.api.resources.Project;

public class EmmaMavenPluginHandler implements MavenPluginHandler {

  public static final String GROUP_ID = MavenUtils.GROUP_ID_CODEHAUS_MOJO;
  public static final String ARTIFACT_ID = "emma-maven-plugin";

  public String getGroupId() {
    return GROUP_ID;
  }

  public String getArtifactId() {
    return ARTIFACT_ID;
  }

  public String getVersion() {
    return "1.0-alpha-2";
  }

  public boolean isFixedVersion() {
    return false;
  }

  public String[] getGoals() {
    return new String[]{"emma"};
  }

  public void configure(Project project, MavenPlugin plugin) {
    plugin.setParameter("format", "xml");

    if (project.getExclusionPatterns() != null) {
      for (String pattern : project.getExclusionPatterns()) {
        if (StringUtils.endsWithIgnoreCase(pattern, ".java")) {
          pattern = StringUtils.substringBeforeLast(pattern, ".");
        }
        pattern = pattern.startsWith("/") ? pattern.substring(1) : pattern;
        pattern = pattern.replace("**", "*").replace('/', '.');
        plugin.addParameter("filters/filter", pattern);
      }
    }
  }

}
