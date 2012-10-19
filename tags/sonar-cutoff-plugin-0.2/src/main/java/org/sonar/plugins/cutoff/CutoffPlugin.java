/*
 * Sonar Cutoff Plugin
 * Copyright (C) 2010 SonarSource
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
package org.sonar.plugins.cutoff;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;

import java.util.Arrays;
import java.util.List;

@Properties({
  @Property(
    key = CutoffConstants.DATE_PROPERTY,
    name = "Cutoff date",
    description = "Only source files updated after this date are analyzed. Format is yyyy-MM-dd, for example 2010-12-25.",
    global = true, project = true, module = true),
  @Property(key = CutoffConstants.PERIOD_IN_DAYS_PROPERTY, name = "Cutoff period",
    description = "Only source files updated during the last X days are analyzed. For example, the value '7' means " +
      "that all the files updated before the last week are excluded from analysis. This property is ignored if " + CutoffConstants.DATE_PROPERTY + " is defined.",
    type = PropertyType.INTEGER,
    global = true, project = true, module = true)
})
public final class CutoffPlugin extends SonarPlugin {

  public List getExtensions() {
    return Arrays.asList(CutoffFilter.class);
  }
}
