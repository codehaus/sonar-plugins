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
package org.sonar.plugins.jlint;

import org.sonar.commons.rules.RulesProfile;
import org.sonar.plugins.api.maven.AbstractMavenPluginHandler;
import org.sonar.plugins.api.maven.model.MavenPlugin;
import org.sonar.plugins.api.maven.model.MavenPom;

import java.io.File;
import java.io.IOException;

public class JlintMavenPluginHandler extends AbstractMavenPluginHandler {
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
    // TODO: Baseline Maven Plugin to 1.0 and update here.
    return "1.1";
  }

  public boolean isFixedVersion() {
    return false;
  }

  public String[] getGoals() {
    return new String[]{"jlint"};
  }

   
  @Override
  public void configurePlugin(MavenPom pom, MavenPlugin plugin) {
    configureClassesDir(pom, plugin);
  
  
    //plugin.setConfigParameter("xmlOutput", "true");
    //plugin.setConfigParameter("threshold", "Low");
    //plugin.setConfigParameter("skip", "false");
    //plugin.setConfigParameter("effort", getEffort(pom), false);
	
	//No dependency added as jlint is an exe in the path.
    //plugin.addDependency("com.symcor.findbugs", "findbugs", "1.3.2", "jar");

    try {
      File xmlFile = saveConfigXml(pom);
      //plugin.setConfigParameter("includeFilterFile", xmlFile.getCanonicalPath());
	  plugin.setConfigParameter("configLocation", xmlFile.getCanonicalPath());
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to save the jlint XML configuration.", e);
    }
	
  }
  

  private void configureClassesDir(MavenPom pom, MavenPlugin plugin) {
    File classesDir = pom.getBuildOutputDir();
    if (classesDir == null || !classesDir.exists()) {
      throw new RuntimeException("Jlint needs sources to be compiled. " +
          "Please edit pom.xml to set the <outputDirectory> node and build before executing sonar."
      );
    }
    try {
      plugin.setConfigParameter("classFilesDirectory", classesDir.getCanonicalPath());
    } catch (Exception e) {
      throw new RuntimeException("Invalid classes directory", e);
    }
  }

  private File saveConfigXml(MavenPom pom) throws IOException {
    String configuration = jlintRulesRepository.exportConfiguration(profile);
    return pom.writeContentToWorkingDirectory(configuration, "jlint-config.xml");
  }
 

  private String getEffort(MavenPom pom) {
    return pom.getConfiguration().getString(JlintPlugin.PROP_EFFORT_KEY, JlintPlugin.PROP_EFFORT_DEFAULTVALUE);
  }

}
