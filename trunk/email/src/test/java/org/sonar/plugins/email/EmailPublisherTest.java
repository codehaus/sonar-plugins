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

import static org.mockito.Mockito.*;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.platform.Server;
import org.sonar.api.resources.Project;
import org.sonar.plugins.email.EmailPublisher.SonarEmail;

public class EmailPublisherTest {
  private SensorContext context;
  private EmailPublisher publisher;
  private Project project;
  private Configuration configuration;
  private SonarEmail email;

  @Before
  public void setUp() {
    context = mock(SensorContext.class);

    Server server = mock(Server.class);
    when(server.getURL()).thenReturn("http://localhost:9000");
    publisher = spy(new EmailPublisher(server));
    email = mock(SonarEmail.class);
    doReturn(email).when(publisher).newEmail();

    project = new Project("org.example:foo", "", "Foo");

    configuration = new BaseConfiguration();
    configuration.setProperty(EmailPublisher.FROM_PROPERTY, "sonar@domain"); // TODO it's mandatory property now - see SONARPLUGINS-968
    project.setConfiguration(configuration);
  }

  @Test
  public void defaultConfiguration() throws Exception {
    configuration.setProperty(EmailPublisher.TO_PROPERTY, "1@domain;2@domain\r\n3@domain 4@domain");

    publisher.getEmail(project);

    verify(email).setHostName("localhost");
    verify(email).setSmtpPort("25");
    verify(email).setTLS(false);
    verify(email).setFrom("sonar@domain");
    verify(email).addTo("1@domain");
    verify(email).addTo("2@domain");
    verify(email).addTo("3@domain");
    verify(email).addTo("4@domain");
    verify(email).setSubject("Sonar analysis of Foo");
    verify(email).setMsg("Sonar analysis of Foo available http://localhost:9000/project/index/org.example:foo");
    verifyNoMoreInteractions(email);
  }

  @Test
  public void enabled() throws Exception {
    configuration.setProperty(EmailPublisher.ENABLED_PROPERTY, true);

    publisher.executeOn(project, context);

    verify(email).send();
  }

  @Test
  public void disabled() throws Exception {
    publisher.executeOn(project, context);

    verify(email, never()).send();
  }

  @Test
  public void shouldSetAuthentication() throws Exception {
    configuration.setProperty(EmailPublisher.USERNAME_PROPERTY, "user");
    configuration.setProperty(EmailPublisher.PASSWORD_PROPERTY, "password");
    publisher.getEmail(project);

    configuration.setProperty(EmailPublisher.USERNAME_PROPERTY, "user");
    configuration.setProperty(EmailPublisher.PASSWORD_PROPERTY, "");
    publisher.getEmail(project);

    configuration.setProperty(EmailPublisher.USERNAME_PROPERTY, "");
    configuration.setProperty(EmailPublisher.PASSWORD_PROPERTY, "password");
    publisher.getEmail(project);

    verify(email).setAuthentication("user", "password");
    verify(email).setAuthentication("", "password");
    verify(email).setAuthentication("user", "");
  }

}
