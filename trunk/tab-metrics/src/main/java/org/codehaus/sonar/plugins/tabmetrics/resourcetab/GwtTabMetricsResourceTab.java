/*
 * Sonar Tab Metrics Plugin
 * Copyright (C) 2012 eXcentia
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
package org.codehaus.sonar.plugins.tabmetrics.resourcetab;

import org.sonar.api.resources.Java;
import org.sonar.api.web.GwtPage;
import org.sonar.api.web.NavigationSection;
import org.sonar.api.web.ResourceLanguage;
import org.sonar.api.web.ResourceQualifier;
import org.sonar.api.web.UserRole;
import org.sonar.wsclient.services.Resource;

/**
 * Gwt TabMetrics Resource Tab
 */
@ResourceLanguage(Java.KEY)
@ResourceQualifier({ Resource.QUALIFIER_FILE, Resource.QUALIFIER_CLASS, Resource.QUALIFIER_PACKAGE, Resource.QUALIFIER_PROJECT, Resource.QUALIFIER_MODULE })
@NavigationSection(NavigationSection.RESOURCE_TAB)
@UserRole(UserRole.USER)
public class GwtTabMetricsResourceTab extends GwtPage {

  /**
   * Gets the TabMetrics Tab Title
   */
  public final String getTitle() {
    return "All";
  }

  /**
   * Obtains the TabMetrics Tab Id
   */
  public final String getGwtId() {
    return "org.codehaus.sonar.plugins.tabmetrics.resourcetab.TabMetricsPluginTab";
  }
}
