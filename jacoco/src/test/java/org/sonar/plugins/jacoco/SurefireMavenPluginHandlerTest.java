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

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.batch.maven.MavenSurefireUtils;
import org.sonar.api.resources.Project;
import org.sonar.api.test.MavenTestUtils;
import org.sonar.api.utils.HttpDownloader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Evgeny Mandrikov
 */
public class SurefireMavenPluginHandlerTest {
  private SurefireMavenPluginHandler handler;

  @Before
  public void setUp() throws Exception {
    HttpDownloader downloader = mock(HttpDownloader.class);
    handler = new SurefireMavenPluginHandler(downloader);
  }

  @Test
  public void testConfigurePlugin() {
    Project project = MavenTestUtils.loadProjectFromPom(getClass(), "pom.xml");
    MavenPlugin plugin = new MavenPlugin(MavenSurefireUtils.GROUP_ID, MavenSurefireUtils.ARTIFACT_ID, MavenSurefireUtils.VERSION);

    handler.configure(project, plugin);

    assertThat(plugin.getParameter("argLine"), Matchers.containsString("-javaagent:"));
  }
}
