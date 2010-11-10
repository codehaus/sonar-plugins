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
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.jacoco;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.sonar.api.BatchExtension;
import org.sonar.api.utils.SonarException;

/**
 * @author Evgeny Mandrikov
 */
public class JaCoCoAgentDownloader implements BatchExtension {

  /**
   * Dirty hack, but it allows to extract agent only once during Sonar analyzes for multi-module project.
   */
  private static File agentJarFile;

  public JaCoCoAgentDownloader() {
  }

  public File getAgentJarFile() {
    if (agentJarFile == null) {
      agentJarFile = extractAgent();
    }
    return agentJarFile;
  }

  private File extractAgent() {
    try {
      InputStream is = getClass().getResourceAsStream("/org/sonar/plugins/jacoco/agent-all-" + JaCoCoVersion.getVersion() + ".jar");
      File agent = File.createTempFile("jacocoagent", ".jar");
      FileUtils.forceDeleteOnExit(agent);
      OutputStream os = FileUtils.openOutputStream(agent);
      IOUtils.copy(is, os);
      JaCoCoUtils.LOG.info("JaCoCo agent extracted: {}", agent);
      return agent;
    } catch (IOException e) {
      throw new SonarException(e);
    }
  }
}
