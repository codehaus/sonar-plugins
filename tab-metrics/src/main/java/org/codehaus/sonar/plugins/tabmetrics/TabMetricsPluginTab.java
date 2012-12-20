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
package org.codehaus.sonar.plugins.tabmetrics;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.DefaultTab;
import org.sonar.api.web.NavigationSection;
import org.sonar.api.web.ResourceQualifier;
import org.sonar.api.web.RubyRailsPage;
import org.sonar.api.web.UserRole;

/**
 * Metrics Tab
 */
@NavigationSection(NavigationSection.RESOURCE_TAB)
@ResourceQualifier({ Qualifiers.FILE, Qualifiers.CLASS })
@UserRole(UserRole.CODEVIEWER)
@DefaultTab(metrics = { CoreMetrics.NCLOC_KEY })
public class TabMetricsPluginTab extends AbstractRubyTemplate implements RubyRailsPage {

  /**
   * Tab ID
   */
  public final String getId() {
    return "metrics";
  }

  /**
   * Tab Title
   */
  public final String getTitle() {
    return "Metrics";
  }

  /**
   * Tab Template path
   */
  @Override
  protected final String getTemplatePath() {
    return "/TabMetrics.html.erb";
  }
}