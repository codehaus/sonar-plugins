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

import org.sonar.api.Plugin;
import java.util.Arrays;
import java.util.List;
import org.sonar.api.Properties;
import org.sonar.api.Property;

/**
 * @author Papapetrou P.Patroklos
 */
@Properties({
    @Property(
        key = GoogleCalendarPublisher.ACCOUNT_PROP,
        name = "Google Account Username",
        description = "Example:googleuser@gmail.com",
        global = false, project = true, module = false),
    @Property(
        key = GoogleCalendarPublisher.PASSWORD_PROP,
        name = "Google Account Password",
        global = false, project = true, module = false),
    @Property(
        key = GoogleCalendarPublisher.ENABLED_PROP,
        name = "Enabled", defaultValue = "false",
        global = false, project = true, module = false),
     @Property(
        key = GoogleCalendarPublisher.CALENDAR_ID_PROP,
        name = "Google Calendar ID",
        description = "Example:c4o4i7m2lbamc4k26sc2vokh5g@group.calendar.google.com",
        global = false, project = true, module = false) })

public class GoogleCalendarPlugin implements Plugin {

  public final List getExtensions() {
    return Arrays.asList(GoogleCalendarPublisher.class);
  }

  @Override
  public final String toString() {
    return getClass().getSimpleName();
  }

  public final String getKey() {
    return "googleCalendar";
  }

  public final String getName() {
    return "Google Calendar";
  }

  public final String getDescription() {
    return "Google Calendar Plugin";
  }
}

