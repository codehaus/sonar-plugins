/*
 * Sonar Comparing Plugin
 * Copyright (C) 2012 David FRANCOIS
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
package org.sonar.plugins.comparing.widget;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;
import org.sonar.api.web.WidgetCategory;
import org.sonar.api.web.WidgetScope;

@WidgetScope(WidgetScope.GLOBAL)
@WidgetCategory("Global")
@UserRole(UserRole.USER)
public class LocByLanguageWidget extends AbstractRubyTemplate implements RubyRailsWidget {

  public String getId() {
    return "loc_by_language";
  }

  public String getTitle() {
    return "LOC by Language";
  }

  @Override
  protected String getTemplatePath() {
    return "/org/sonar/plugins/comparing/loc_by_language.html.erb";
  }

}
