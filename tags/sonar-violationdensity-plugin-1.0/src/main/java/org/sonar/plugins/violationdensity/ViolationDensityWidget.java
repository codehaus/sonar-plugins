/*
 * Sonar Violation Density Plugin
 * Copyright (C) 2011 MACIF
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
package org.sonar.plugins.violationdensity;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.Description;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.WidgetCategory;

@WidgetCategory({ "Rules" })
@Description("Reports violations and violation density on coding standards.")
public class ViolationDensityWidget extends AbstractRubyTemplate implements RubyRailsWidget {

  public String getId() {
    return "violationdensity";
  }

  public String getTitle() {
    return "Violation Density";
  }

  @Override
  protected String getTemplatePath() {
    return "/org/sonar/plugins/violationdensity/widgets/violationdensity.html.erb";
  }
}