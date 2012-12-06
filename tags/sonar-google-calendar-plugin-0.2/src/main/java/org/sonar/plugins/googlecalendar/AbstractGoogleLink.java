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

import com.google.api.client.util.Key;
import java.util.List;

/**
 * @author Papapetrou P.Patroklos
 */

public class AbstractGoogleLink {

  @Key("@href")
  public String href;

  @Key("@rel")
  public String rel;

  public static String find(List<AbstractGoogleLink> links, String rel) {
    if (links != null) {
      for (AbstractGoogleLink link : links) {
        if (rel.equals(link.rel)) {
          return link.href;
        }
      }
    }
    return null;
  }
}
