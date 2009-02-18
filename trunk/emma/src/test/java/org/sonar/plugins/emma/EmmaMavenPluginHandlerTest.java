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
package org.sonar.plugins.emma;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.sonar.plugins.api.maven.MavenTestCase;
import org.sonar.plugins.api.maven.model.MavenPlugin;
import org.sonar.plugins.api.maven.model.MavenPom;

public class EmmaMavenPluginHandlerTest extends MavenTestCase {
  protected EmmaMavenPluginHandler handler;

  @Before
  public void before() {
    handler = new EmmaMavenPluginHandler();
  }

  @Test
  public void shouldConfigurePom() {
    MavenPom pom = readMavenProject("/org/sonar/plugins/emma/EmmaMavenPluginHandlerTest/pom.xml");
    handler.configure(pom);

    MavenPlugin plugin = pom.findPlugin(handler.getGroupId(), handler.getArtifactId());
    assertEquals(handler.getVersion(), plugin.getVersion());

    assertEquals("xml", plugin.getConfigParameter("format"));
    assertEquals("true", plugin.getConfigParameter("quiet"));
  }

  @Test
  public void shouldOverrideExistingConfiguration() {
    MavenPom pom = readMavenProject("/org/sonar/plugins/emma/EmmaMavenPluginHandlerTest/Emma-pom.xml");
    handler.configure(pom);

    MavenPlugin plugin = pom.findPlugin(handler.getGroupId(), handler.getArtifactId());
    assertEquals("0.0.1", plugin.getVersion()); // keep the version from pom

    assertEquals("xml", plugin.getConfigParameter("format"));
    assertEquals("true", plugin.getConfigParameter("quiet"));
    assertEquals("bar", plugin.getConfigParameter("foo"));
  }
}
