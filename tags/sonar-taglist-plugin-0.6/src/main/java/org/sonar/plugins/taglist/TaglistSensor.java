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
package org.sonar.plugins.taglist;

import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.maven.DependsUponMavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.RulesManager;

import java.io.File;
import java.io.IOException;

public class TaglistSensor implements Sensor, DependsUponMavenPlugin {

  private TaglistViolationsXmlParser taglistParser;
  private TaglistMavenPluginHandler pluginHandler;

  public TaglistSensor(RulesManager rulesManager, RulesProfile rulesProfile, TaglistMavenPluginHandler pluginHandler) {
    taglistParser = new TaglistViolationsXmlParser(rulesManager, rulesProfile);
    this.pluginHandler = pluginHandler;
  }

  public boolean shouldExecuteOnProject(Project project) {
    return project.getLanguage().equals(Java.INSTANCE);
  }

  public MavenPluginHandler getMavenPluginHandler(Project project) {
    return pluginHandler;
  }

  public void analyse(Project project, SensorContext context) {
    File xmlFile = project.getFileSystem().getFileFromBuildDirectory("taglist/taglist.xml");
    try {
      taglistParser.populateTaglistViolation(context, xmlFile);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
