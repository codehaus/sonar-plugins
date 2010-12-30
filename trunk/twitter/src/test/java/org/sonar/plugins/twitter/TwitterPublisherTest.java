/*
 * Sonar Twitter Plugin
 * Copyright (C) 2010 SonarSource
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

package org.sonar.plugins.twitter;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sonar.plugins.twitter.TwitterPlugin.HOST_DEFAULT_VALUE;
import static org.sonar.plugins.twitter.TwitterPlugin.HOST_PROPERTY;
import static org.sonar.plugins.twitter.TwitterPlugin.PASSWORD_PROPERTY;
import static org.sonar.plugins.twitter.TwitterPlugin.USERNAME_PROPERTY;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;

import twitter4j.TwitterException;

/**
 * @author Evgeny Mandrikov
 */
public class TwitterPublisherTest {

  private TwitterPublisher publisher;
  private Project project;
  private SensorContext context;

  @Before
  public void setUp() {
    publisher = spy(new TwitterPublisher());
    project = mock(Project.class);
    context = mock(SensorContext.class);
  }

  @Test
  public void updateStatus() throws TwitterException {
    Configuration configuration = new BaseConfiguration();
    configuration.setProperty(USERNAME_PROPERTY, "user");
    configuration.setProperty(PASSWORD_PROPERTY, "pass");
    configuration.setProperty(HOST_PROPERTY, HOST_DEFAULT_VALUE);

    when(project.getConfiguration()).thenReturn(configuration);
    when(project.getName()).thenReturn("SimpleProject");
    doNothing().when(publisher).updateStatus(anyString());

    publisher.executeOn(project, context);

    // verify(publisher).updateStatus(contains("SimpleProject"));
  }

  @Test
  public void dontUpdateStatusIfUsernameAndPasswordNotSpecified() throws TwitterException {
    Configuration configuration = mock(Configuration.class);
    when(configuration.getString(USERNAME_PROPERTY)).thenReturn("akram");
    when(configuration.getString(PASSWORD_PROPERTY)).thenReturn("akram");
    when(configuration.getString(HOST_PROPERTY, HOST_DEFAULT_VALUE)).thenReturn(HOST_DEFAULT_VALUE);
    when(project.getConfiguration()).thenReturn(configuration);

    publisher.executeOn(project, context);

    verify(publisher, never()).updateStatus(anyString());
  }
}
