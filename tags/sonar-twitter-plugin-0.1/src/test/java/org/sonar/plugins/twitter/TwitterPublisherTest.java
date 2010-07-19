/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010 SonarSource
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

package org.sonar.plugins.twitter;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

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
  public void updateStatus() {
    Configuration configuration = new BaseConfiguration();
    configuration.setProperty(TwitterPlugin.USERNAME_PROPERTY, "user");
    configuration.setProperty(TwitterPlugin.PASSWORD_PROPERTY, "pass");
    when(project.getConfiguration()).thenReturn(configuration);
    when(project.getName()).thenReturn("SimpleProject");
    doNothing().when(publisher).updateStatus(anyString(), anyString(), anyString());

    publisher.executeOn(project, context);

    verify(publisher).updateStatus(eq("user"), eq("pass"), contains("SimpleProject"));
  }

  @Test
  public void dontUpdateStatusIfUsernameAndPasswordNotSpecified() {
    when(project.getConfiguration()).thenReturn(new BaseConfiguration());

    publisher.executeOn(project, context);

    verify(publisher, never()).updateStatus(anyString(), anyString(), anyString());
  }

}
