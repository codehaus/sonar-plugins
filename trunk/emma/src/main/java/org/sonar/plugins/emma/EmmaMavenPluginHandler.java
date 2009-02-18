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

import org.sonar.plugins.api.maven.*;
import org.sonar.plugins.api.maven.model.*;

public class EmmaMavenPluginHandler extends AbstractMavenPluginHandler {

  public String getGroupId() {
    return MavenPom.GROUP_ID_CODEHAUS_MOJO;
  }

  public String getArtifactId() {
    return "emma-maven-plugin";
  }

  public String getVersion() {
    return "1.0-alpha-1";
  }

  public boolean isFixedVersion() {
    return false;
  }

  public String[] getGoals() {
    return new String[]{"emma"};
  }

  public boolean shouldStopOnFailure() {
    return true;
  }

  @Override
  public void configurePlugin(MavenPom pom, MavenPlugin plugin) {
    plugin.unsetConfigParameter( "outputDirectory" );
    plugin.setConfigParameter( "format", "xml" );
    plugin.setConfigParameter("quiet", "true" );
  }
}
