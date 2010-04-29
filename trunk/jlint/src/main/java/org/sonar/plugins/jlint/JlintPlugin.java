/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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

package org.sonar.plugins.jlint;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import java.util.ArrayList;
import java.util.List;

@Properties({
    @Property(
        key = JlintPlugin.PROP_EFFORT_KEY,
        defaultValue = JlintPlugin.PROP_EFFORT_DEFAULTVALUE,
        name = "Effort",
        description = "Effort of the bug finders. Valid values are Min, Default and Max. Setting 'Max' increases precision but also increases memory consumption.<br/>" +
            "This parameter can be overriden on projects : set the property <code>" + JlintPlugin.PROP_EFFORT_KEY + "</code> in maven (pom.xml property or mvn argument).",
        project = true,
        module = true,
        global = true)
})
public class JlintPlugin implements Plugin {

  public static final String KEY = "jlint";
  public static final String PROP_EFFORT_KEY = "sonar.jlint.effort";
  public static final String PROP_EFFORT_DEFAULTVALUE = "Max";

  public String getKey() {
    return KEY;
  }

  public String getName() {
    return "Jlint";
  }

  public String getDescription() {
    return "Jlint will check your Java code and find bugs, inconsistencies and synchronization problems by doing data flow analysis and building the lock graph.  Jlint is extremely fast - even on large projects, it requires only one second to check all classes! You can find more by going to the <a href='http://jlint.sourceforge.net'>Jlint web site</a>. \nJlint groups its rules into categories.  A category can be enabled or diabled through the configuration xml file.  Once the configuration is loaded into Sonar, you can identify which rules belong to a category, by going to the quality profile configuation and selecting a filter for Jlint rules. Each rule starts with the name of its category.  \nIf rules are activated/deactivated through the quality profile configuration screen, you must ensure that all the rules belonging to a category are either enabled or disabled. Else you will encounter an error while running this plugin. \nThis plugin supports Jlint version 3.1 only.";
  }

  public List<Class<? extends Extension>> getExtensions() {
    List<Class<? extends Extension>> list = new ArrayList<Class<? extends Extension>>();
    list.add(JlintSensor.class);
    list.add(JlintMavenPluginHandler.class);
    list.add(JlintRulesRepository.class);
    return list;
  }

}
