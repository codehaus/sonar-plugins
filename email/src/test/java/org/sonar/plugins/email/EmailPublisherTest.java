/*
 * Sonar Email Plugin
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

package org.sonar.plugins.email;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.platform.Server;
import org.sonar.api.resources.Project;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmailPublisherTest {
  private EmailPublisher publisher;

  @Before
  public void setUp() {
    Server server = mock(Server.class);
    when(server.getURL()).thenReturn("http://localhost:9000");
    publisher = new EmailPublisher(server);
  }

  @Test
  public void test() {
    Project project = new Project("org.example:foo", "", "Foo");

    assertThat(publisher.getSubject(project), is("Sonar analysis of Foo"));
    assertThat(publisher.getMessage(project), is("Sonar analysis of Foo available http://localhost:9000/project/index/org.example:foo"));
  }
}
