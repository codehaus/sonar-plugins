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
package org.sonar.plugins.jtest;

import org.apache.commons.lang.StringUtils;
import org.sonar.plugins.api.maven.AbstractMavenPluginHandler;
import org.sonar.plugins.api.maven.Exclusions;
import org.sonar.plugins.api.maven.model.MavenPlugin;
import org.sonar.plugins.api.maven.model.MavenPom;

public class JtestMavenPluginHandler extends AbstractMavenPluginHandler {

  public static final String GROUP_ID = MavenPom.GROUP_ID_CODEHAUS_MOJO;
  public static final String ARTIFACT_ID = "jtest-maven-plugin";
  
  private Exclusions exclusions;

  public JtestMavenPluginHandler(Exclusions exclusions) {
    super();
    this.exclusions = exclusions;
  }

  public String getGroupId() {
    return GROUP_ID;
  }

  public String getArtifactId() {
    return ARTIFACT_ID;
  }

  public String getVersion() {
    return "1.0-alpha-1";
  }

  public boolean isFixedVersion() {
    return false;
  }

  public String[] getGoals() {
    return new String[]{"jtest"};
  }

  public boolean shouldStopOnFailure() {
    return true;
  }

  @Override
  public void configurePlugin(MavenPom pom, MavenPlugin plugin) {
    plugin.unsetConfigParameter("outputDirectory");
    plugin.setConfigParameter("format", "xml");
    plugin.setConfigParameter("quiet", "true");

    if (exclusions != null) {
      for (String pattern : exclusions.getWildcardPatterns()) {
        if (StringUtils.endsWithIgnoreCase(pattern, ".java")) {
          pattern = StringUtils.substringBeforeLast(pattern, ".");
        }
        pattern = pattern.startsWith("/") ? pattern.substring(1) : pattern;
        pattern = pattern.replace("**", "*").replace('/', '.');
        plugin.getConfiguration().addParameter("filters/filter", pattern);
      }
    }
  }
}
