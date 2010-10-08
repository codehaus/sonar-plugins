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

import org.sonar.api.Plugin;
import org.sonar.api.Extension;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import java.util.List;
import java.util.ArrayList;

@Properties({
    @Property(
        key = TwitterPlugin.USERNAME_PROPERTY,
        name = "Username",
        global = true,
        project = true,
        module = false
    ),
    @Property(
        key = TwitterPlugin.PASSWORD_PROPERTY,
        name = "Password",
        global = true,
        project = true,
        module = false
    )
})
public class TwitterPlugin implements Plugin {

  public static final String USERNAME_PROPERTY = "sonar.twitter.username.secured";
  public static final String PASSWORD_PROPERTY = "sonar.twitter.password.secured";
  public static final String HOST_PROPERTY = "sonar.host.url";
  public static final String HOST_DEFAULT_VALUE = "http://localhost:9000";

  public String getKey() {
    return "twitter";
  }

  public String getName() {
    return "Twitter";
  }

  public String getDescription() {
    return "Reports about analysis via <a href='http://twitter.com/'>Twitter</a>.";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
    list.add(TwitterPublisher.class);
    return list;
  }

}
