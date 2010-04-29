/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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

package org.sonar.plugins.jlint;

import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.io.IOException;

public class JlintMavenPluginHandler implements MavenPluginHandler {
  private RulesProfile profile;
  private JlintRulesRepository jlintRulesRepository;

  public JlintMavenPluginHandler(RulesProfile profile, JlintRulesRepository jlintRulesRepository) {
    this.profile = profile;
    this.jlintRulesRepository = jlintRulesRepository;
  }

  public String getGroupId() {
    return "com.symcor.jlint.plugin";
  }

  public String getArtifactId() {
    return "maven-jlint-plugin";
  }

  public String getVersion() {
    return "1.2";
  }

  public boolean isFixedVersion() {
    return false;
  }

  public String[] getGoals() {
    return new String[]{"jlint"};
  }

  public void configure(Project project, MavenPlugin plugin) {
    configureClassesDir(project, plugin);
    try {
      File xmlFile = saveConfigXml(project);
      //plugin.setConfigParameter("includeFilterFile", xmlFile.getCanonicalPath());
      plugin.setParameter("configLocation", xmlFile.getCanonicalPath());
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to save the jlint XML configuration.", e);
    }
  }


  private void configureClassesDir(Project project, MavenPlugin plugin) {
    File classesDir = project.getFileSystem().getBuildOutputDir();
    if (classesDir == null || !classesDir.exists()) {
      throw new SonarException("Jlint needs sources to be compiled. " +
          "Please edit pom.xml to set the <outputDirectory> node and build before executing sonar."
      );
    }
    try {
      plugin.setParameter("classFilesDirectory", classesDir.getCanonicalPath());
    } catch (Exception e) {
      throw new SonarException("Invalid classes directory", e);
    }
  }

  private File saveConfigXml(Project project) throws IOException {
    String configuration = jlintRulesRepository.exportConfiguration(profile);
    return project.getFileSystem().writeToWorkingDirectory(configuration, "jlint-config.xml");
  }


  private String getEffort(Project project) {
    return project.getConfiguration().getString(JlintPlugin.PROP_EFFORT_KEY, JlintPlugin.PROP_EFFORT_DEFAULTVALUE);
  }

}
