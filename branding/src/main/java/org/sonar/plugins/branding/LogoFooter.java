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

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.web.Footer;

public class LogoFooter implements Footer {

  private final Logger logger = LoggerFactory.getLogger(LogoFooter.class);

  private final Configuration configuration;

  public LogoFooter(Configuration configuration) {
    this.configuration = configuration;
  }

  private String getImageUrl() {
    return configuration.getString(BrandingPlugin.IMAGE_PROPERTY, "");
  }

  private LogoLocation getLogoLocation() {
    String locationStr = configuration.getString(BrandingPlugin.LOGO_LOCATION_PROPERTY, "TOP");
    LogoLocation location;
    try {
      location = LogoLocation.valueOf(locationStr);
    } catch (IllegalArgumentException e) {
      logger.warn("Invalid value for property " + BrandingPlugin.LOGO_LOCATION_PROPERTY + ". Using TOP as default.");
      location = LogoLocation.TOP;
    }
    return location;
  }

  public String getHtml() {
    String imageUrl = getImageUrl();
    if (StringUtils.isEmpty(imageUrl)) {
      return "";
    }

    StringBuffer sb = new StringBuffer();
    sb.append("<script type=\"text/javascript\">\n");
    sb.append("    Event.observe(window, 'load', function(){\n");

    sb.append("        var companyLogo = document.createElement('img');\n");
    sb.append("        companyLogo.setAttribute('src', '").append(imageUrl).append("');\n");
    sb.append("        companyLogo.setAttribute('alt', 'Company Logo');\n");
    sb.append("        companyLogo.setAttribute('title', 'Company');\n");

    switch (getLogoLocation()) {
      case TOP :
        sb.append("        var sonarContent = $$('div[id=\"error\"]').first().parentNode;\n");
        sb.append("        sonarContent.insertBefore(companyLogo, sonarContent.firstChild);\n");
        break;
      case MENU :
        sb.append("        var sonarLogo = $$('img[title=\"Embrace Quality\"]').first();\n");
        sb.append("        var center = $($(sonarLogo.parentNode).parentNode);\n");
        sb.append("        center.appendChild(companyLogo);\n");
        break;
      default:
        logger.warn("Location no supported");
    }
    sb.append("    });\n");
    sb.append("</script>\n");
    return sb.toString();
  }

}
