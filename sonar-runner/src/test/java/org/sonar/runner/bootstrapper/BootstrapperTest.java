/*
 * Sonar Standalone Runner
 * Copyright (C) 2011 SonarSource
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
package org.sonar.runner.bootstrapper;

import org.junit.Test;
import org.sonar.runner.bootstrapper.Bootstrapper;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

public class BootstrapperTest {

  @Test
  public void shouldRemoveLastUrlSlash() {
    Bootstrapper bootstrapper = new Bootstrapper("", "http://test/", new File("target"));
    assertThat(bootstrapper.getServerUrl()).isEqualTo("http://test");
  }

  @Test(expected = Exception.class)
  public void shouldFailIfCanNotConnectServer() {
    Bootstrapper bootstrapper = new Bootstrapper("", "http://unknown.foo", new File("target"));
    bootstrapper.getServerVersion();
  }

  @Test
  public void shouldReturnUserAgent() {
    Bootstrapper bootstrapper = new Bootstrapper("test/0.1", "http://unknown.foo", new File("target"));
    String userAgent = bootstrapper.getUserAgent();

    assertThat(userAgent.length()).isGreaterThan(0);
    assertThat(userAgent).startsWith("sonar-bootstrapper/");
    assertThat(userAgent).endsWith(" test/0.1");
  }

  @Test
  public void shouldReturnValidVersion() {
    Bootstrapper bootstrapper = new Bootstrapper("", "http://test", new File("target")) {
      @Override
      String remoteContent(String path) throws IOException {
        return "2.6";
      }
    };
    assertThat(bootstrapper.getServerVersion()).isEqualTo("2.6");
  }

}
