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

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.client.xml.atom.AtomParser;

/**
 * @author Papapetrou P.Patroklos
 */

public class GoogleHttpTransportFactory {

  public static final HttpTransport DEFAULT_TRANSPORT = newTransport(false);
  public static final HttpTransport AUTH_TRANSPORT = newTransport(true);

  private GoogleHttpTransportFactory() {
    
  }
  
  static HttpTransport newTransport(boolean isAuthentication) {
    HttpTransport transport = new NetHttpTransport();
    GoogleUtils.useMethodOverride(transport);
    GoogleHeaders headers = new GoogleHeaders();
    headers.setApplicationName("sonar-google-calendar-plugin-0.1");
    transport.defaultHeaders = headers;
    if (!isAuthentication) {

      headers.gdataVersion = "2";
      AtomParser parser = new AtomParser();
      parser.namespaceDictionary = new XmlNamespaceDictionary().set("", "http://www.w3.org/2005/Atom").set(
              "batch", "http://schemas.google.com/gdata/batch").set(
              "gd", "http://schemas.google.com/g/2005");


      transport.addParser(parser);
    }
    return transport;
  }
}
