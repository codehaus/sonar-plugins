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

package com.ots.ejustice.sonar.plugin.google.calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.configuration.Configuration;
import org.sonar.api.platform.Server;

/**
 * @author Papapetrou P.Patroklos
 */
public class GoogleCalendarPublisher implements PostJob {

    /** Sonar Property for Google Account. */
    public static final String ACCOUNT_PROP
            = "sonar.google.calendar.account";
    /** Sonar Property for Google Account Password. */
    public static final String PASSWORD_PROP
            = "sonar.google.calendar.password";
    /** Sonar Property for Google Calendar ID. */
    public static final String CALENDAR_ID_PROP
            = "sonar.google.calendar.calendarname";
    /** Sonar Property for Enabling / Disabling Google Calendar Plugin. */
    public static final String ENABLED_PROP
            = "sonar.google.calendar.enabled";
    /** Project Base URI. */
    private static final String PROJECT_BASE_URI = "/project/index/";

    /** Google Feeds URL. */
    private static final String GOOGLE_FEEDS =
            "http://www.google.com/calendar/feeds/";

    /** Google Feeds URL. */
    private static final String GOOGLE_PRIV_CAL = "/private/full";
    /** Class Logger using SL4J. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GoogleCalendarPublisher.class);

    /** Sonar Server. */
    private final Server server;

    public GoogleCalendarPublisher(final Server serverPrm) {
        this.server = serverPrm;
    }

    @Override
    public final void executeOn(final Project prj,
            final SensorContext sensorContext) {
        final Configuration configuration = prj.getConfiguration();
        final String username = configuration.getString(ACCOUNT_PROP);
        final String password = configuration.getString(PASSWORD_PROP);
        final String calendarID = configuration.getString(CALENDAR_ID_PROP);
        final String isEnabled = configuration.getString(ENABLED_PROP);

        if (isEnabled != null && isEnabled.equals("true")) {

            final CalendarService calService =
                    new CalendarService("sonar-google-calendar-plugin-0.1");
            try {
                calService.setUserCredentials(username, password);
                final URL postURL = new URL(this.getCalendarURL(calendarID));
                final CalendarEventEntry newCalEvent = new CalendarEventEntry();

                newCalEvent.setTitle(
                        new PlainTextConstruct(this.getTitle(prj)));
                newCalEvent.setContent(
                        new PlainTextConstruct(this.getContent(prj)));

                final DateTime startTime = DateTime.now();
                final DateTime endTime = DateTime.now();
                final When eventTimes = new When();
                eventTimes.setStartTime(startTime);
                eventTimes.setEndTime(endTime);
                newCalEvent.addTime(eventTimes);

                calService.insert(postURL, newCalEvent);
            } catch (AuthenticationException ex) {
                LOGGER.error("Cannot authenicate. Please check your google account/username settings", ex);
            } catch (MalformedURLException ex) {
                LOGGER.error("Cannot connect to Google Calendar.Please check your google Calendar ID settings", ex);
            } catch (IOException ex) {
                LOGGER.error("IOException", ex);
            } catch (ServiceException ex) {
                LOGGER.error("Cannot Connect to Google API.Please try again later", ex);
            }
        }
    }
    
    public final String getTitle(final Project project) {
        return String.format("Sonar analysis of %s", project.getName());
    }

    public final String getContent(final Project project) {
        final StringBuilder url =
                new StringBuilder(server.getURL()).
                append(PROJECT_BASE_URI).append(project.getKey());
        return String.format(
                "New Sonar analysis of %s is available online at %s",
                project.getName(), url);
    }

    public final String getCalendarURL(final String calendarID) {
        final StringBuilder calendarURL =
                new StringBuilder(GOOGLE_FEEDS);
        calendarURL.append(calendarID);
        calendarURL.append(GOOGLE_PRIV_CAL);
        return calendarURL.toString();
    }
}
