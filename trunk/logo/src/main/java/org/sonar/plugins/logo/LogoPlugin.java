/*
 * Sonar Logo Plugin
 * Copyright (C) 2011 SonarSource
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

package org.sonar.plugins.logo;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

@Properties({ @Property(key = LogoPlugin.URL_PROPERTY, name = "Logo URL", global = true) })
public class LogoPlugin implements Plugin {

  public static final String URL_PROPERTY = "sonar.logo.url";

  public String getKey() {
    return "logo";
  }

  public String getName() {
    return "";
  }

  public String getDescription() {
    return "";
  }

  public List getExtensions() {
    return Arrays.asList(LogoFooter.class);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
