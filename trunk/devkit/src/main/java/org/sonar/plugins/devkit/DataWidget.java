/*
 * Sonar Development Kit Plugin
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

package org.sonar.plugins.devkit;

import org.sonar.api.web.*;

@WidgetCategory({"Devkit"})
@Description("List available data")
@UserRole("admin")
public class DataWidget extends AbstractRubyTemplate implements RubyRailsWidget {
  public String getId() {
    return "devkit-data";
  }

  public String getTitle() {
    return "Debug Data";
  }

  @Override
  protected String getTemplatePath() {
    return "/org/sonar/plugins/devkit/data_widget.html.erb";
  }
}
