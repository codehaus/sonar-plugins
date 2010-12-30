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

import static org.sonar.plugins.twitter.TwitterPlugin.PASSWORD_PROPERTY;
import static org.sonar.plugins.twitter.TwitterPlugin.TWITTER_TOKEN;
import static org.sonar.plugins.twitter.TwitterPlugin.TWITTER_TOKEN_ID;
import static org.sonar.plugins.twitter.TwitterPlugin.TWITTER_TOKEN_SECRET;
import static org.sonar.plugins.twitter.TwitterPlugin.USERNAME_PROPERTY;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

@Properties({ @Property(key = USERNAME_PROPERTY, name = "Username", global = true, project = true, module = false),
  @Property(key = PASSWORD_PROPERTY, name = "Password", global = true, project = true, module = false),
  @Property(key = TWITTER_TOKEN, name = "Twitter token", global = true, project = true, module = false),
  @Property(key = TWITTER_TOKEN_SECRET, name = "Twitter secret token", global = true, project = true, module = false),
  @Property(key = TWITTER_TOKEN_ID, name = "Twitter id", global = true, project = true, module = false) })
public class TwitterPlugin implements Plugin {

  public static final String USERNAME_PROPERTY = "sonar.twitter.username.secured";
  public static final String PASSWORD_PROPERTY = "sonar.twitter.password.secured";
  public static final String TWITTER_TOKEN = "sonar.twitter.token";
  public static final String TWITTER_TOKEN_SECRET = "sonar.twitter.token.secret";
  public static final String TWITTER_TOKEN_ID = "sonar.twitter.token.id";

  public static final String HOST_PROPERTY = "sonar.host.url";
  public static final String HOST_DEFAULT_VALUE = "http://localhost:9000";

  public final String getKey() {
    return "twitter";
  }

  public final String getName() {
    return "Twitter";
  }

  public final String getDescription() {
    return "Reports about analysis via <a href='http://twitter.com/'>Twitter</a>.";
  }

  public final List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
    list.add(TwitterPublisher.class);
    return list;
  }

}
