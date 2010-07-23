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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenPluginHandler;
import org.sonar.api.batch.maven.MavenSurefireUtils;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.HttpDownloader;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Evgeny Mandrikov
 */
public class SurefireMavenPluginHandler implements MavenPluginHandler {
  private static final String ARG_LINE_PARAMETER = "argLine";

  private static String agentPath;
  private HttpDownloader downloader;

  public SurefireMavenPluginHandler(HttpDownloader downloader) {
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

  protected String getAgentPath(Project project) {
    if (agentPath == null) {
      agentPath = downloadAgent(project);
    }
    return agentPath;
  }

  private String downloadAgent(Project project) {
    try {
      String host = project.getConfiguration().getString("sonar.host.url", "http://localhost:9000");
      URI uri = new URI(host + "/deploy/plugins/sonar-jacoco-plugin/agent-all-0.4.0.20100604151516.jar");
      File agent = File.createTempFile("jacocoagent", ".jar");
      downloader.download(uri, agent);
      FileUtils.forceDeleteOnExit(agent);
      LoggerFactory.getLogger(getClass()).info("Agent: {}", agent);
      return agent.getAbsolutePath();
    } catch (IOException e) {
      throw new SonarException(e);
    } catch (URISyntaxException e) {
      throw new SonarException(e);
    }
  }

  public void configure(Project project, MavenPlugin surefirePlugin) {
    String argLine = surefirePlugin.getParameter(ARG_LINE_PARAMETER);
    String agent = "-javaagent:" + getAgentPath(project) + "=destfile=" + JaCoCoSensor.getPath(project);
    argLine = StringUtils.isBlank(argLine) ? agent : agent + " " + argLine;
    surefirePlugin.setParameter(ARG_LINE_PARAMETER, argLine);
  }

}
