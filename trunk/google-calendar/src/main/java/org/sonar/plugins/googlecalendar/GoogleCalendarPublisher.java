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

import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.http.HttpResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;

import java.io.IOException;
import org.apache.commons.configuration.Configuration;
import org.sonar.api.platform.Server;

/**
 * @author Papapetrou P.Patroklos
 */
public class GoogleCalendarPublisher implements PostJob {

    /** Sonar Property for Google Account. */
    public static final String ACCOUNT_PROP = "sonar.google.calendar.account";
    /** Sonar Property for Google Account Password. */
    public static final String PASSWORD_PROP = "sonar.google.calendar.password";
    /** Sonar Property for Google Calendar ID. */
    public static final String CALENDAR_ID_PROP = "sonar.google.calendar.calendarname";
    /** Sonar Property for Enabling / Disabling Google Calendar Plugin. */
    public static final String ENABLED_PROP = "sonar.google.calendar.enabled";
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

            ClientLogin authenticator = new ClientLogin();
            authenticator.authTokenType = "cl";
            authenticator.username = username;
            authenticator.password = password;
            authenticator.transport = GoogleHttpTransportFactory.AUTH_TRANSPORT;

            try {
                authenticator.authenticate().setAuthorizationHeader(GoogleHttpTransportFactory.DEFAULT_TRANSPORT);
                
                GoogleCalendarUrl calendarUrl = new GoogleCalendarUrl(this.getCalendarURL(calendarID));
                GoogleEventEntry newEvent = new GoogleEventEntry();
                newEvent.title = this.getTitle(prj);
                newEvent.content = this.getContent(prj);
                newEvent.executeInsert(calendarUrl);
            } catch (HttpResponseException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            } catch (IOException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
        }
    }

    public final String getTitle(final Project project) {
        return String.format("Sonar analysis of %s", project.getName());
    }

    public final String getContent(final Project project) {
        final StringBuilder url =
                new StringBuilder(server.getURL()).append(PROJECT_BASE_URI).append(project.getKey());
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
