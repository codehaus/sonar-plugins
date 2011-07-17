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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.platform.Server;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;

import java.io.IOException;
import java.net.ServerSocket;

public class EmailSendingTest {

  private static int port;

  private SimpleSmtpServer server;
  private EmailPublisher publisher;
  private Project project;
  private SensorContext context;

  @BeforeClass
  public static void selectPort() {
    port = getNextAvailablePort();
  }

  @Before
  public void start() {
    server = SimpleSmtpServer.start(port);

    Configuration configuration = new BaseConfiguration();
    configuration.setProperty(EmailPublisher.ENABLED_PROPERTY, true);
    configuration.setProperty(EmailPublisher.PORT_PROPERTY, Integer.toString(port));
    configuration.setProperty(EmailPublisher.FROM_PROPERTY, "sonar@domain");
    configuration.setProperty(EmailPublisher.TO_PROPERTY, "user@domain");
    project = new Project("org.example:foo", "", "Foo");
    project.setConfiguration(configuration);

    Server server = mock(Server.class);
    when(server.getURL()).thenReturn("http://localhost:9000");
    publisher = new EmailPublisher(server);

    context = mock(SensorContext.class);
  }

  @After
  public void stop() {
    if (!server.isStopped()) {
      server.stop();
    }
  }

  @Test
  public void shouldSendEmail() {
    publisher.executeOn(project, context);

    assertThat(server.getReceivedEmailSize(), is(1));
    SmtpMessage email = (SmtpMessage) server.getReceivedEmail().next();
    assertThat(email.getHeaderValue("From"), containsString("<sonar@domain>"));
    assertThat(email.getHeaderValue("To"), containsString("<user@domain>"));
    assertThat(email.getHeaderValue("Subject"), is("Sonar analysis of Foo"));
    assertThat(email.getBody(), is("Sonar analysis of Foo available http://localhost:9000/project/index/org.example:foo"));
  }

  @Test(expected = SonarException.class)
  public void shouldThrowAnExceptionWhenUnableToSendEmail() {
    server.stop();
    publisher.executeOn(project, context);
  }

  private static int getNextAvailablePort() {
    try {
      ServerSocket socket = new ServerSocket(0);
      int unusedPort = socket.getLocalPort();
      socket.close();
      return unusedPort;
    } catch (IOException e) {
      throw new RuntimeException("Error getting an available port from system", e);
    }
  }

}
