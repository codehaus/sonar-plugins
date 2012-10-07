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
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;

@Properties({
  @Property(key = BrandingPlugin.IMAGE_PROPERTY,
    name = "Image URL",
    description = "Example : http://codehaus.org/codehaus-small.gif"),
  @Property(key = BrandingPlugin.IMAGE_WIDTH,
    type=org.sonar.api.PropertyType.INTEGER,
    name = "Image width in pixels",
    description = "If empty or zero image is displayed in default width. Example : 80",
    defaultValue = "0"),
  @Property(key = BrandingPlugin.IMAGE_HEIGHT,
    name = "Image height in pixels",
    type=org.sonar.api.PropertyType.INTEGER,
    description = "If empty or zero image is displayed in default height. Example : 80",
    defaultValue = "0"),
  @Property(key = BrandingPlugin.LINK_PROPERTY,
    name = "Link URL",
    description = "Example : http://codehaus.org/"),
  @Property(key = BrandingPlugin.LOGO_LOCATION_PROPERTY,
    name = "Logo location in Sonar UI",
    description = "Possible values: TOP, MENU", defaultValue = "TOP") })
public class BrandingPlugin extends SonarPlugin {

  public static final String IMAGE_PROPERTY = "sonar.branding.image";
  public static final String IMAGE_WIDTH = "sonar.branding.image.width";
  public static final String IMAGE_HEIGHT = "sonar.branding.image.height";
  public static final String LINK_PROPERTY = "sonar.branding.link";
  public static final String LOGO_LOCATION_PROPERTY = "sonar.branding.logo.location";

  public List getExtensions() {
    return Arrays.asList(LogoFooter.class, ProjectLogoWidget.class);
  }

}
