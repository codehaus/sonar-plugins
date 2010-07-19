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
package org.sonar.plugins.emma;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import java.util.ArrayList;
import java.util.List;

@Properties({
    @Property(
        key = EmmaSensor.PROP_REPORT_PATH,
        name = "Report file",
        description = "Path (absolute or relative) to Emma XML report. Do not set value in order to use default maven settings.",
        module = true,
        project = true,
        global = false
    )
})
public class EmmaPlugin implements Plugin {

  public String getKey() {
    return "emma";
  }

  public String getName() {
    return "Emma";
  }

  public String getDescription() {
    return "<a href='http://emma.sourceforge.net'>Emma</a> calculates coverage of unit tests. Set the parameter 'Code coverage plugin' to <code>emma</code> in the General plugin.";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
    list.add(EmmaSensor.class);
    list.add(EmmaMavenPluginHandler.class);
    list.add(ProjectCoverageDecorator.class);
    return list;
  }

  public String toString() {
    return getKey();
  }
}
