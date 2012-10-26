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

import org.junit.Test;
import org.sonar.api.config.Settings;

import static org.fest.assertions.Assertions.assertThat;

public class CloverSettingsTest {

  @Test
  public void should_support_deprecated_property() {
    // before sonar 3.4
    Settings settings = new Settings();

    settings.setProperty("sonar.core.codeCoveragePlugin", "clover,phpunit");
    assertThat(new CloverSettings(settings).isEnabled()).isTrue();

    settings.setProperty("sonar.core.codeCoveragePlugin", "cobertura");
    assertThat(new CloverSettings(settings).isEnabled()).isFalse();
  }

  @Test
    public void should_support_sonar_3_4_property() {
      // since sonar 3.4
      Settings settings = new Settings();

      settings.setProperty("sonar.java.coveragePlugin", "clover");
      assertThat(new CloverSettings(settings).isEnabled()).isTrue();

    settings.setProperty("sonar.java.coveragePlugin", "jacoco");
      assertThat(new CloverSettings(settings).isEnabled()).isFalse();
    }
}
