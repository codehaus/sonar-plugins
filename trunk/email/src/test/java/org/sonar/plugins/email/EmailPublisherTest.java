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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.mail.Email;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.platform.Server;
import org.sonar.api.resources.Project;
import org.sonar.plugins.email.EmailPublisher.SonarEmail;

public class EmailPublisherTest {
  private EmailPublisher publisher;
  private Project project;
  private Configuration configuration;

  @Before
  public void setUp() {
    Server server = mock(Server.class);
    when(server.getURL()).thenReturn("http://localhost:9000");
    publisher = new EmailPublisher(server);
    project = new Project("org.example:foo", "", "Foo");
    configuration = new BaseConfiguration();
    project.setConfiguration(configuration);
  }

  @Test
  public void multipleRecipients() throws Exception {
    configuration.setProperty(EmailPublisher.FROM_PROPERTY, "sonar@domain");
    configuration.setProperty(EmailPublisher.TO_PROPERTY, "1@domain;2@domain\r\n3@domain 4@domain");

    Email email = publisher.getEmail(project);

    assertThat(email.getToAddresses().size(), is(4));
  }

  @Test
  public void defaultConfiguration() throws Exception {
    configuration.setProperty(EmailPublisher.FROM_PROPERTY, "sonar@domain"); // TODO it's mandatory property now - see SONARPLUGINS-968

    Email email = publisher.getEmail(project);

    assertThat(email.getHostName(), is("localhost"));
    assertThat(email.isTLS(), is(false));
    assertThat(email.getFromAddress().getAddress(), is("sonar@domain"));
    assertThat(email.getToAddresses().size(), is(0));
  }

  @Test
  public void enabled() throws Exception {
    configuration.setProperty(EmailPublisher.ENABLED_PROPERTY, true);
    SensorContext context = mock(SensorContext.class);
    publisher = spy(publisher);
    SonarEmail email = mock(SonarEmail.class);
    doReturn(email).when(publisher).getEmail(project);

    publisher.executeOn(project, context);

    verify(email).send();
  }

  @Test
  public void disabled() throws Exception {
    SensorContext context = mock(SensorContext.class);
    publisher = spy(publisher);
    SonarEmail email = mock(SonarEmail.class);
    doReturn(email).when(publisher).getEmail(project);

    publisher.executeOn(project, context);

    verify(email, never()).send();
  }

  @Test
  public void test() {
    assertThat(publisher.getSubject(project), is("Sonar analysis of Foo"));
    assertThat(publisher.getMessage(project), is("Sonar analysis of Foo available http://localhost:9000/project/index/org.example:foo"));
  }
}
