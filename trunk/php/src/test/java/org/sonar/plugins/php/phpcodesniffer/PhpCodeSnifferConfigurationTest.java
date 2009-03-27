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

package org.sonar.plugins.php.phpcodesniffer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.sonar.plugins.api.maven.model.MavenPom;

import java.io.IOException;

public class PhpCodeSnifferConfigurationTest {

  @Test
  public void shouldGetCommandLineForWindows() {
    PhpCodeSnifferConfiguration config = getWindowsConfiguration();
    assertThat(config.getCommandLine(), is(PhpCodeSnifferConfiguration.COMMAND_LINE + ".bat"));
  }

  @Test
  public void shouldGetCommandLineForNotWindows() {
    PhpCodeSnifferConfiguration config = getNotWindowsConfiguration();
    assertThat(config.getCommandLine(), is(PhpCodeSnifferConfiguration.COMMAND_LINE));
  }

  @Test
  public void shouldGetCommandLineWithPath() {
    String path = "path/to/phpcodesniffer";
    PhpCodeSnifferConfiguration config = getConfiguration(false, path);
    assertThat(config.getCommandLine(), is(path + "/" + PhpCodeSnifferConfiguration.COMMAND_LINE));
  }

  @Test
  public void shouldGetCommandLineWithPathEvenIfExistingLastSlash() {
    String path = "path/to/phpcodesniffer";
    PhpCodeSnifferConfiguration config = getConfiguration(false, path + "/");
    assertThat(config.getCommandLine(), is(path + "/" + PhpCodeSnifferConfiguration.COMMAND_LINE));
  }

  @Test
  public void shouldReportFileBeInTargetDir() throws IOException {
    MavenPom pom = mock(MavenPom.class);
    PhpCodeSnifferConfiguration config = new PhpCodeSnifferConfiguration(pom, null);
    config.getReportFileCommandOption();
    verify(pom).getBuildDir();
  }

  @Test
  public void shouldReplaceSpaceByUnderscoreInProperProfileName(){
    PhpCodeSnifferConfiguration config = new PhpCodeSnifferConfiguration(null, "profile name with space");
    String result = config.getCleanProfileName();
    assertThat(result, is("profile_name_with_space"));
  }

  @Test
  public void shouldGetValidSuffixeOption(){
    PhpCodeSnifferConfiguration config = new PhpCodeSnifferConfiguration(null, null);
    String suffixesOption = config.getSuffixesCommandOption();
    assertThat(suffixesOption, notNullValue());
    assertThat(suffixesOption, containsString(","));
  }

  private PhpCodeSnifferConfiguration getWindowsConfiguration() {
    return getConfiguration(true, "");
  }

  private PhpCodeSnifferConfiguration getNotWindowsConfiguration() {
    return getConfiguration(false, "");
  }

  private PhpCodeSnifferConfiguration getConfiguration(final boolean isOsWindows, final String path) {
    PhpCodeSnifferConfiguration config = new PhpCodeSnifferConfiguration() {

      protected String getCommandLinePath() {
        return path;
      }

      protected boolean isOsWindows() {
        return isOsWindows;
      }
    };
    return config;
  }
}
