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

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.sonar.api.resources.Project;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Evgeny Mandrikov
 */
public class JaCoCoAgentDownloaderTest {

  @Test
  public void testGetDownloadUrl() {
    Project project = mock(Project.class);
    Configuration configuration = new BaseConfiguration();
    when(project.getConfiguration()).thenReturn(configuration);

    JaCoCoAgentDownloader downloader = new JaCoCoAgentDownloader(configuration);

    configuration.setProperty("sonar.host.url", "http://localhost:9000");
    assertThat(downloader.getDownloadUrl(), startsWith("http://localhost:9000/deploy/plugins/sonar-jacoco-plugin/agent-all"));

    configuration.setProperty("sonar.host.url", "http://localhost:9000/");
    assertThat(downloader.getDownloadUrl(), startsWith("http://localhost:9000/deploy/plugins/sonar-jacoco-plugin/agent-all"));
  }

}
