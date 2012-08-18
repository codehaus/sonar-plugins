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
package org.sonar.plugins.comparing;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;
import org.sonar.api.web.WidgetCategory;
import org.sonar.api.web.WidgetProperties;
import org.sonar.api.web.WidgetProperty;
import org.sonar.api.web.WidgetPropertyType;
import org.sonar.api.web.WidgetScope;

@WidgetScope(WidgetScope.GLOBAL)
@WidgetCategory("Global")
@UserRole(UserRole.USER)
@WidgetProperties(
{
  @WidgetProperty(key = "metricDisplay", description=MeasureByLanguageWidget.PROPERTY_DESC, 
        type = WidgetPropertyType.STRING, defaultValue = "PRJ")
})
public class MeasureByLanguageWidget extends AbstractRubyTemplate implements RubyRailsWidget {
    
  public static final String PROPERTY_DESC = "<ul class=\"bullet\">"
          + "<li>PRJ to display the number of projects by language</li>"
          + "<li>LOC to display the sum of number of lines of code by language</li></ul>";

  public String getId() {
    return "measure_by_language";
  }

  public String getTitle() {
    return "Measure by Language";
  }

  @Override
  protected String getTemplatePath() {
    return "/org/sonar/plugins/comparing/measure_by_language.html.erb";
  }

}
