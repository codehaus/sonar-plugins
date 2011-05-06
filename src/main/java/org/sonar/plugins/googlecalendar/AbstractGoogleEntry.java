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

import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.DataUtil;
import com.google.api.client.util.Key;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.client.xml.atom.AtomContent;

import java.io.IOException;
import java.util.List;

/**
 * @author Papapetrou P.Patroklos
 */

public class AbstractGoogleEntry implements Cloneable {

  @Key
  public String summary;

  @Key
  public String title;

  @Key
  public String updated;

  @Key("link")
  public List<AbstractGoogleLink> links;

  @Override
  protected AbstractGoogleEntry clone() throws CloneNotSupportedException {
    return DataUtil.clone(this);
  }

  AbstractGoogleEntry executeInsert(GoogleCalendarUrl url) throws IOException {
    HttpRequest request = GoogleHttpTransportFactory.DEFAULT_TRANSPORT.buildPostRequest();
    request.url = url;
    AtomContent content = new AtomContent();
    content.namespaceDictionary = new XmlNamespaceDictionary().set("", "http://www.w3.org/2005/Atom").set(
          "batch", "http://schemas.google.com/gdata/batch").set(
          "gd", "http://schemas.google.com/g/2005");

    content.entry = this;
    request.content = content;
    return GoogleCalendarRedirectHandler.execute(request).parseAs(getClass());
  }
}
