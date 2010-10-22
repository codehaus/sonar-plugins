/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.cutoff;

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import java.util.Arrays;
import java.util.List;

@Properties({
    @Property(key=CutoffConstants.DATE_PROPERTY, name="Cutoff date",
        description = "Only source files updated after this date are analyzed. Format is yyyy-MM-dd, for example 2010-12-25.",
        global = true, project = true, module = true),
    @Property(key=CutoffConstants.PERIOD_IN_DAYS_PROPERTY, name="Cutoff period",
        description = "Only source files updated during the last X days are analyzed. For example, the value '7' means " +
            "that all the files updated before the last week are excluded from analysis. This property is ignored if " + CutoffConstants.DATE_PROPERTY + " is defined.", 
        global = true, project = true, module = true)
})
public final class CutoffPlugin implements Plugin {

  public String getKey() {
    return "cutoff";
  }

  public String getName() {
    return "Cutoff";
  }

  public String getDescription() {
    return "";
  }

  public List getExtensions() {
    return Arrays.asList(CutoffFilter.class);
  }
}
