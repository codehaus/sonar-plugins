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

import java.io.IOException;

/**
 * @author Papapetrou P.Patroklos
 */

public class GoogleCalendarEntry extends AbstractGoogleEntry {

    public String getEventFeedLink() {
        return AbstractGoogleLink.find(links, "http://schemas.google.com/gCal/2005#eventFeed");
    }

    @Override
    public GoogleCalendarEntry clone() {
        return (GoogleCalendarEntry) super.clone();
    }

    @Override
    public GoogleCalendarEntry executeInsert(GoogleCalendarUrl url) throws IOException {
        return (GoogleCalendarEntry) super.executeInsert(url);
    }

}
