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
package org.sonar.plugins.reviewinfo;

import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.io.IOException;

public class ReviewInfoMavenPluginHandler implements MavenPluginHandler {
  private RulesProfile profile;
  //private JlintRulesRepository jlintRulesRepository;

  public ReviewInfoMavenPluginHandler() {
  }

  public String getGroupId() {
    return "org.codehaus.mojo";
  }

  public String getArtifactId() {
    return "maven-reviewinfo-plugin";
  }

  public String getVersion() {
    // TODO: Baseline Maven Plugin to 1.0 and update here.
    return "1.0";
  }

  public boolean isFixedVersion() {
    return false;
  }

  public String[] getGoals() {
    return new String[]{"reviewinfo"};
  }


  public void configure(Project project, MavenPlugin plugin) {
     ;
  }

}
