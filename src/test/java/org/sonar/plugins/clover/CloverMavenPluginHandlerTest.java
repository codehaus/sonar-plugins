/*
 * Sonar Clover Plugin
 * Copyright (C) 2008 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.clover;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.maven.MavenPlugin;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.test.MavenTestUtils;

import static org.fest.assertions.Assertions.assertThat;

public class CloverMavenPluginHandlerTest {

  private CloverMavenPluginHandler handler;
  private Project project;
  private MavenPlugin plugin;
  private Settings settings;

  @Before
  public void init() {
    settings = new Settings(new PropertyDefinitions(CloverPlugin.class));
    handler = new CloverMavenPluginHandler(settings);
  }

  private void configurePluginHandler(String pom) {
    project = MavenTestUtils.loadProjectFromPom(getClass(), pom);
    plugin = MavenPlugin.getPlugin(project.getPom(), handler.getGroupId(), handler.getArtifactId());
    handler.configure(project, plugin);
  }
  
  @Test
  public void test_metadata() {
    assertThat(handler.getGroupId()).isEqualTo("com.atlassian.maven.plugins");
    assertThat(handler.getArtifactId()).isEqualTo("maven-clover2-plugin");
    assertThat(handler.getVersion()).isEqualTo("3.0.5");
  }

  @Test
  public void overrideConfiguration() throws Exception {
    configurePluginHandler("overrideConfiguration.xml");

    assertThat(plugin.getParameter("generateXml")).isEqualTo("true");
    assertThat(plugin.getParameter("foo")).isEqualTo("bar");
    String configuredReportPath = handler.getSettings().getString(CoreProperties.SUREFIRE_REPORTS_PATH_PROPERTY);
    assertThat(configuredReportPath).isNotNull();
    configuredReportPath = configuredReportPath.replace('\\', '/');
    assertThat(configuredReportPath).endsWith("clover/surefire-reports");
  }

  @Test
  public void shouldSkipCloverWithPomConfig() throws Exception {
    configurePluginHandler("shouldSkipCloverWithPomConfig.xml");

    assertThat(handler.getSettings().getString(CoreProperties.SUREFIRE_REPORTS_PATH_PROPERTY)).isNull();
  }

  @Test
  public void shouldSkipCloverWithPomProperty() throws Exception {
    // Because we are using a mocked Settings, Maven properties will not be present in Settings so we set it manually
    settings.setProperty("maven.clover.skip", true);
    configurePluginHandler("shouldSkipCloverWithPomProperty.xml");
    assertThat(handler.getSettings().getString(CoreProperties.SUREFIRE_REPORTS_PATH_PROPERTY)).isNull();
  }

}
