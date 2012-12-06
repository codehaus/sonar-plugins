/*
 * Google Calendar Plugin
 * Copyright (C) 2011 OTS SA
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
package org.sonar.plugins.googlecalendar;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.sonar.api.config.Settings;
import org.sonar.api.platform.Server;
import org.sonar.api.resources.Project;

public class GoogleCalendarPublisherTest {

  private transient GoogleCalendarPublisher publisher;
  private transient Project project;
  private transient Settings settings = new Settings();

  @Before
  public final void setUp() {
    Server server = mock(Server.class);
    when(server.getURL()).thenReturn("http://localhost:9000");
    settings.setProperty(GoogleCalendarPublisher.CALENDAR_ID_PROP, "123@group.calendar.google.com");
    settings.setProperty(GoogleCalendarPublisher.ENABLED_PROP, "true");
    settings.setProperty(GoogleCalendarPublisher.ACCOUNT_PROP, "user@gmail.com");
    settings.setProperty(GoogleCalendarPublisher.PASSWORD_PROP, "password");
    publisher = new GoogleCalendarPublisher(server, settings);
    project = new Project("com.ots.ejustice.example:dummy", "", "Dummy");
  }

  @Test
  public final void testGoogleCalendarURL() {
    assertThat(publisher.getCalendarURL(settings.getString(GoogleCalendarPublisher.CALENDAR_ID_PROP)),
            is("http://www.google.com/calendar/feeds/123@group.calendar.google.com/private/full"));
  }

  @Test
  public final void testEventTitle() {
    assertThat(publisher.getTitle(project), is("Sonar analysis of Dummy"));
  }

  @Test
  public final void testEventContent() {
    assertThat(publisher.getContent(project),
            is("New Sonar analysis of Dummy is available online at http://localhost:9000/project/index/com.ots.ejustice.example:dummy"));
  }

  @Test
  public final void testExecuteOn() {
    publisher.executeOn(project, null);
  }
}
