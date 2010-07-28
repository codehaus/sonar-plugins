/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SonarSource
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
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.jacoco;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.jacoco.core.runtime.AgentOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.batch.maven.MavenSurefireUtils;
import org.sonar.api.resources.Project;

import java.io.File;

/**
 * @author Evgeny Mandrikov
 */
public class SurefireMavenPluginHandler implements MavenPluginHandler {
  private static final String ARG_LINE_PARAMETER = "argLine";

  private JaCoCoAgentDownloader downloader;

  public SurefireMavenPluginHandler(JaCoCoAgentDownloader downloader) {
    this.downloader = downloader;
  }

  public String getGroupId() {
    return MavenSurefireUtils.GROUP_ID;
  }

  public String getArtifactId() {
    return MavenSurefireUtils.ARTIFACT_ID;
  }

  public String getVersion() {
    return MavenSurefireUtils.VERSION;
  }

  public boolean isFixedVersion() {
    return false;
  }

  public String[] getGoals() {
    return new String[]{"test"};
  }

  public void configure(Project project, MavenPlugin surefirePlugin) {
    Logger logger = LoggerFactory.getLogger(getClass());

    // See SONARPLUGINS-600
    String destfilePath = JaCoCoSensor.getPath(project);
    File destfile = project.getFileSystem().resolvePath(destfilePath);
    if (destfile.exists() && destfile.isFile()) {
      logger.info("Deleting {}", destfile);
      destfile.delete();
    }

    Configuration configuration = project.getConfiguration();
    AgentOptions options = new AgentOptions();
    options.setDestfile(destfilePath);
    String includes = configuration.getString(JaCoCoPlugin.INCLUDES_PROPERTY);
    if (StringUtils.isNotBlank(includes)) {
      options.setIncludes(includes);
    }
    String excludes = configuration.getString(JaCoCoPlugin.EXCLUDES_PROPERTY);
    if (StringUtils.isNotBlank(excludes)) {
      options.setExcludes(excludes);
    }
    String argument = options.getVMArgument(downloader.getAgentJarFile());

    String argLine = surefirePlugin.getParameter(ARG_LINE_PARAMETER);
    argLine = StringUtils.isBlank(argLine) ? argument : argument + " " + argLine;
    logger.info("JVM options: {}", argLine);
    surefirePlugin.setParameter(ARG_LINE_PARAMETER, argLine);
  }

}
