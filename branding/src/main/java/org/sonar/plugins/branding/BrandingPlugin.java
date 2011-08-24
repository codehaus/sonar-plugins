/*
 * Sonar Branding Plugin
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

package org.sonar.plugins.branding;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

@Properties({ 
    @Property(key = BrandingPlugin.IMAGE_PROPERTY,
    name = "Image URL",
    description = "You need to restart server to changes take effect. Example : http://www.codehaus.org/codehaus-small.gif"),
    @Property(key = BrandingPlugin.LOGO_LOCATION_PROPERTY,
            name = "Logo location in Sonar UI",
            description = "Possible values: TOP, MENU", defaultValue="TOP")})
public class BrandingPlugin implements Plugin {

  public static final String IMAGE_PROPERTY = "sonar.branding.image";
  public static final String LOGO_LOCATION_PROPERTY = "sonar.branding.logo.location";

  public String getKey() {
    return "branding";
  }

  public String getName() {
    return "Branding";
  }

  public String getDescription() {
    return "Allows to add your own logo to the Sonar UI.";
  }

  public List getExtensions() {
    return Arrays.asList(LogoFooter.class, ProjectLogoWidget.class);
  }

}
