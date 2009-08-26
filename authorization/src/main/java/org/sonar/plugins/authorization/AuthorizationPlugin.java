/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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
package org.sonar.plugins.authorization;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import java.util.ArrayList;
import java.util.List;

@Properties({
    @Property(
        key = "sonar.authorization.viewer",
        defaultValue = "EVERYONE",
        name = "Viewers",
        description = "Only the users in these groups can view the project.",
        project = true,
        global = false),
    @Property(
        key = "sonar.authorization.admin",
        defaultValue = "LOGIN_USER",
        name = "Administrators",
        description = "Only the users in these groups can admin the project/system.",
        project = true,
        global = true)
})
public class AuthorizationPlugin implements Plugin {
  public static final String PLUGIN_KEY = "authorization";

  public String getKey() {
    return PLUGIN_KEY;
  }

  public String getName() {
    return "Authorization";
  }

  public String getDescription() {
    String desc = "<b>Specify the authorized groups. </b><br/>" +
        " * <i>EVERYONE</i> is every user including the guest.<br/>" +
        " * <i>LOGIN_USER</i> is the user who logged in the system.<br/>" +
        " You can specify more than one group, separate them by \",\".";
    return desc;
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> extensions = new ArrayList<Class<? extends Extension>>();
    return extensions;
  }

  public String toString() {
    return getKey();
  }
}
