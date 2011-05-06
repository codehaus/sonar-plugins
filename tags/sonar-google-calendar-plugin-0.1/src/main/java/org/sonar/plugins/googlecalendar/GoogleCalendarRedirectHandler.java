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

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.http.HttpExecuteIntercepter;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;

import java.io.IOException;

/**
 * @author Papapetrou P.Patroklos
 */

public final class GoogleCalendarRedirectHandler {
  
  private GoogleCalendarRedirectHandler(){
    
  }
  
  private static final class SessionIntercepter implements HttpExecuteIntercepter {

    private String gsessionid;

    private SessionIntercepter(HttpTransport transport, GoogleUrl locationUrl) {
      this.gsessionid = (String) locationUrl.getFirst("gsessionid");
      transport.removeIntercepters(SessionIntercepter.class);
      transport.intercepters.add(0, this); 
    }

    public void intercept(HttpRequest request) {
      request.url.set("gsessionid", this.gsessionid);
    }
  }

  static HttpResponse execute(HttpRequest request) throws IOException {
    try {
      return request.execute();
    } catch (HttpResponseException e) {
      if (e.response.statusCode == 302) {
        GoogleUrl url = new GoogleUrl(e.response.headers.location);
        request.url = url;
        new SessionIntercepter(request.transport, url);
        e.response.ignore(); // force the connection to close
        return request.execute();
      } else {
        throw e;
      }
    }
  }
}
