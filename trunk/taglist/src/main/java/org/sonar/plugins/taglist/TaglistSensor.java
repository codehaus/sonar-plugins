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

import java.io.File;
import java.io.IOException;

import org.sonar.api.rules.RulesManager;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.maven.MavenPluginExecutor;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.ProjectUtils;

public class TaglistSensor implements Sensor {

  private TaglistViolationsXmlParser taglistParser;
  private MavenPluginExecutor mavenExecutor;
  private TaglistMavenPluginHandler pluginHandler;

  public TaglistSensor(RulesManager rulesManager, RulesProfile rulesProfile, MavenPluginExecutor mavenExecutor, TaglistMavenPluginHandler pluginHandler) {
    taglistParser = new TaglistViolationsXmlParser(rulesManager, rulesProfile);
    this.mavenExecutor = mavenExecutor;
    this.pluginHandler = pluginHandler;
  }

  public boolean shouldExecuteOnProject(Project project) {
    return project.getLanguage().equals(Java.KEY);
  }
  public void analyse(Project pom, SensorContext context) {
    mavenExecutor.execute(pluginHandler);
    File xmlFile = ProjectUtils.getFileFromBuildDirectory(pom, "taglist/taglist.xml");
    try {
      taglistParser.populateTaglistViolation(context, pom, xmlFile);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
